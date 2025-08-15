package net.nekoepisode.tankserver.game.player;

import net.nekoepisode.tankserver.command.CommandSender;
import net.nekoepisode.tankserver.game.GameInstance;
import net.nekoepisode.tankserver.game.color.Color;
import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.tank.PlayerTank;
import net.nekoepisode.tankserver.game.tank.TankManager;
import net.nekoepisode.tankserver.network.PlayerConnection;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.event.events.EventAnnounceConnection;
import net.nekoepisode.tankserver.network.event.events.EventKick;
import net.nekoepisode.tankserver.network.event.events.EventPlayerChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Player implements CommandSender {
    private static final Logger log = LoggerFactory.getLogger(Player.class);

    private final UUID uuid;
    private final String name;
    private final PlayerConnection playerConnection;
    private final PlayerSettings playerSettings;

    private PlayerTank tank = null;
    private volatile GameInstance currentGameInstance = null;
    private volatile PlayerState playerState = PlayerState.IN_LOBBY;
    private volatile boolean isRemoving = false;

    public Player(UUID uuid, String name, PlayerConnection playerConnection) {
        this.uuid = uuid;
        this.name = name;
        this.playerConnection = playerConnection;
        this.playerSettings = new PlayerSettings(
                new TankColor(
                        new Color(0, 150, 255),
                        new Color(32, 107, 159),
                        new Color(16, 128, 207)
                ) // Default blue tank
        );
    }

    public void sendEvent(INetworkEvent event) {
        if (isConnectionValid() && !isRemoving) {
            try {
                playerConnection.sendEvent(event);
            } catch (Exception e) {
                log.error("Error sending event {} to player {}", event.getClass().getSimpleName(), name, e);
                // Don't call remove() here to avoid infinite recursion
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        if (isConnectionValid() && !isRemoving) {
            sendEvent(new EventPlayerChat("Server", message));
        }
    }

    public void kick(String reason) {
        if (isRemoving) return;

        if (isConnectionValid()) {
            try {
                sendEvent(new EventKick(reason));
                playerConnection.ctx().close().addListener(future -> {
                    remove();
                });
            } catch (Exception e) {
                log.error("Error kicking player {}", name, e);
                remove();
            }
        } else {
            remove();
        }
    }

    public void remove() {
        synchronized (this) {
            if (isRemoving) return;
            isRemoving = true;

            log.info("Removing player {} from server", name);
        }

        try {
            // Remove from current game instance first
            GameInstance gameInstance = this.currentGameInstance;
            if (gameInstance != null) {
                gameInstance.removePlayer(this);
                this.currentGameInstance = null;
            }

            // Remove from player manager
            PlayerManager.getInstance().removePlayer(this);

            // Clean up tank
            if (this.tank != null) {
                try {
                    TankManager.getInstance().removeTank(this.tank);
                } catch (Exception e) {
                    log.error("Error removing tank for player {}", name, e);
                }
                this.tank = null;
            }

            // Announce disconnection to other players
            announceDisconnection();

        } catch (Exception e) {
            log.error("Error during player {} removal", name, e);
        }

        log.info("Player {} removed successfully", name);
    }

    private void announceDisconnection() {
        try {
            EventAnnounceConnection disconnectEvent = new EventAnnounceConnection(this, false);
            for (Player player : PlayerManager.getInstance().getPlayers()) {
                if (player != this && player.isConnectionValid() && !player.isRemoving) {
                    try {
                        player.sendEvent(disconnectEvent);
                    } catch (Exception e) {
                        log.error("Error announcing disconnection to player {}", player.getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error announcing disconnection for player {}", name, e);
        }
    }

    private boolean isConnectionValid() {
        return playerConnection != null &&
                playerConnection.ctx() != null &&
                playerConnection.ctx().channel() != null &&
                playerConnection.ctx().channel().isActive();
    }

    // Getters and setters
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    public void setTank(PlayerTank tank) {
        this.tank = tank;
    }

    public PlayerTank getTank() {
        return tank;
    }

    public void setCurrentGameInstance(GameInstance currentGameInstance) {
        this.currentGameInstance = currentGameInstance;
    }

    public GameInstance getCurrentGameInstance() {
        return currentGameInstance;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public PlayerSettings getPlayerSettings() {
        return playerSettings;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', uuid=" + uuid + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}