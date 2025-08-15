package net.nekoepisode.tankserver.game;

import net.nekoepisode.tankserver.game.level.Level;
import net.nekoepisode.tankserver.game.level.PlayerSpawn;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.game.player.PlayerState;
import net.nekoepisode.tankserver.game.tank.PlayerTank;
import net.nekoepisode.tankserver.game.tank.Tank;
import net.nekoepisode.tankserver.game.tank.TankManager;
import net.nekoepisode.tankserver.network.event.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameInstance {
    private static final Logger log = LoggerFactory.getLogger(GameInstance.class);
    private final String id;
    private final Level level;

    private final List<Player> players;
    private final List<Tank> tanks;

    private static int threadCount = 0;

    private Timer timer;
    private volatile boolean isShuttingDown = false;

    public GameInstance(Level level) {
        this.level = level;
        this.players = new CopyOnWriteArrayList<>();
        this.tanks = new CopyOnWriteArrayList<>();
        this.id = level.getName();
    }

    public void addPlayer(Player player) {
        synchronized (this) {
            if (isShuttingDown) {
                log.warn("Cannot add player {} to shutting down game instance", player);
                return;
            }

            if (players.contains(player)) return;
            players.add(player);

            try {
                player.sendEvent(new EventLoadLevel(level.getLevelString(), 400, level.isDisableFriendlyFire()));
                player.sendEvent(new EventEnterLevel());

                PlayerSpawn playerSpawn = null;
                try {
                    playerSpawn = level.getPlayerSpawns().get(players.size() - 1);
                } catch (IndexOutOfBoundsException e) {
                    log.warn("No player spawn found for player {}", player);
                }

                if (playerSpawn == null) {
                    safelyRemovePlayer(player);
                    safeSendMessage(player, "We can't found spawn point for you :(");
                    return;
                }

                PlayerTank playerTank = new PlayerTank(player.getPlayerSettings().getTankColor(),
                        playerSpawn.x, playerSpawn.y, playerSpawn.angle, 0, playerSpawn.team);

                int tankId = TankManager.getInstance().getRandomNetworkId();
                TankManager.getInstance().addTank(tankId, playerTank);
                log.debug("Player {} has spawned at {} {} {}, id {}", player,
                        playerTank.getX(), playerTank.getY(), playerTank.getAngle(), tankId);

                player.setTank(playerTank);
                tanks.add(playerTank);

                broadcastNewPlayerTank(player, playerTank, tankId);

                player.setCurrentGameInstance(this);
                player.setPlayerState(PlayerState.INGAME_WAITING);
                safeSendMessage(player, "You have been spawned!");

            } catch (Exception e) {
                log.error("Error adding player {} to game instance", player, e);
                safelyRemovePlayer(player);
            }
        }
    }

    private void broadcastNewPlayerTank(Player newPlayer, PlayerTank playerTank, int tankId) {
        List<Player> playersCopy = List.copyOf(players);

        for (Player existingPlayer : playersCopy) {
            if (!isPlayerValid(existingPlayer)) continue;

            try {
                existingPlayer.sendEvent(new EventTankPlayerCreate(
                        newPlayer,
                        playerTank.getX(),
                        playerTank.getY(),
                        playerTank.getAngle(),
                        playerTank.getTeam(),
                        tankId,
                        0
                ));

                if (!existingPlayer.equals(newPlayer) && existingPlayer.getTank() != null) {
                    newPlayer.sendEvent(new EventTankPlayerCreate(
                            existingPlayer,
                            existingPlayer.getTank().getX(),
                            existingPlayer.getTank().getY(),
                            existingPlayer.getTank().getAngle(),
                            existingPlayer.getTank().getTeam(),
                            TankManager.getInstance().getNetworkId(existingPlayer.getTank()),
                            0
                    ));
                }
            } catch (Exception e) {
                log.error("Error broadcasting tank creation for player {}", existingPlayer, e);
            }
        }
    }

    public void removePlayer(Player player) {
        synchronized (this) {
            if (!players.contains(player)) return;

            log.info("Removing player {} from game instance {}", player, id);

            players.remove(player);
            if (player.getTank() != null) {
                tanks.remove(player.getTank());

                int tankNetworkId = TankManager.getInstance().getNetworkId(player.getTank());
                broadcastTankRemoval(tankNetworkId, player);
            }

            player.setCurrentGameInstance(null);

            if (players.isEmpty() && !isShuttingDown) {
                clean();
            }
        }
    }

    private void broadcastTankRemoval(int tankNetworkId, Player removedPlayer) {
        List<Player> playersCopy = List.copyOf(players);

        for (Player player : playersCopy) {
            if (!isPlayerValid(player) || player.equals(removedPlayer)) continue;

            try {
                player.sendEvent(new EventTankRemove(tankNetworkId, true));
            } catch (Exception e) {
                log.error("Error sending tank removal event to player {}", player, e);
            }
        }
    }

    private void safelyRemovePlayer(Player player) {
        try {
            players.remove(player);
            if (player.getTank() != null) {
                tanks.remove(player.getTank());
            }
        } catch (Exception e) {
            log.error("Error during safe player removal", e);
        }
    }

    private void safeSendMessage(Player player, String message) {
        try {
            if (isPlayerValid(player)) {
                player.sendMessage(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to player {}: {}", player, message, e);
        }
    }

    private boolean isPlayerValid(Player player) {
        return player != null &&
                player.getPlayerConnection() != null &&
                player.getPlayerConnection().ctx() != null &&
                player.getPlayerConnection().ctx().channel().isActive();
    }

    public void startLoop() {
        new Thread(() -> { // FIXME: 1 GameInstance = 1 Thread? Maybe need to change this
            Thread.currentThread().setName("GameInstanceLoopThread-" + threadCount);
            threadCount++;
            log.info("{} start loop", this);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!isShuttingDown) {
                        update();
                    }
                }
            }, 0, 1000 / 60); // 60 ticks per second
        }).start();
    }

    public void stopLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void update() {
        try {
            for (Tank tank : tanks) {
                if (tank != null) {
                    tank.update();
                }
            }
        } catch (Exception e) {
            log.error("Error during game instance update", e);
        }
    }

    public void clean() {
        synchronized (this) {
            if (isShuttingDown) return;
            isShuttingDown = true;

            log.info("Cleaning up game instance {}", id);

            List<Player> playersCopy = List.copyOf(players);

            for (Player player : playersCopy) {
                if (isPlayerValid(player)) {
                    try {
                        player.sendMessage("Game instance closed! All players will be exit to the lobby!");
                        player.sendEvent(new EventLevelExit("gameInstanceClose"));
                        player.setPlayerState(PlayerState.IN_LOBBY);
                        player.setCurrentGameInstance(null);
                    } catch (Exception e) {
                        log.error("Error notifying player {} of game instance closure", player, e);
                    }
                }
            }

            stopLoop();
            players.clear();
            tanks.clear();
            GameInstanceManager.getInstance().removeGameInstance(this);

            log.info("Game instance {} cleaned up successfully", id);
        }
    }

    public Level getLevel() {
        return level;
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public List<Tank> getTanks() {
        return List.copyOf(tanks);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GameInstance{id='" + id + "', players=" + players.size() + "}";
    }
}