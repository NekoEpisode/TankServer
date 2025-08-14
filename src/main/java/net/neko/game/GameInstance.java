package net.neko.game;

import net.neko.game.level.Level;
import net.neko.game.level.PlayerSpawn;
import net.neko.game.player.Player;
import net.neko.game.tank.PlayerTank;
import net.neko.game.tank.Tank;
import net.neko.network.event.events.EventEnterLevel;
import net.neko.network.event.events.EventLoadLevel;
import net.neko.network.event.events.EventTankPlayerCreate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameInstance {
    private final String id;
    private final Level level;

    private final List<Player> players;
    private final List<Tank> tanks;

    public GameInstance(Level level) {
        this.level = level;
        this.players = new CopyOnWriteArrayList<>();
        this.tanks = new CopyOnWriteArrayList<>();
        this.id = level.getName();
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.sendEvent(new EventLoadLevel(level.getLevelString(), 400, level.isDisableFriendlyFire()));
        player.sendEvent(new EventEnterLevel());

        PlayerSpawn playerSpawn = null;
        try {
            playerSpawn = level.getPlayerSpawns().get(players.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No player spawn found for player " + player);
        }

        if (playerSpawn == null) {
            player.sendServerMessage("We can't found spawn point for you :(");
            return;
        }

        PlayerTank playerTank = new PlayerTank(player.getTankColor(), playerSpawn.x, playerSpawn.y, playerSpawn.team);

        player.setPlayerTank(playerTank);
        tanks.add(playerTank);
        player.sendEvent(new EventTankPlayerCreate(
                player,
                playerTank.getX(),
                playerTank.getY(),
                0,
                player.getTank().getTeam(),
                0,
                0
        ));
        player.sendServerMessage("You have been spawned!");
    }

    public void removePlayer(Player player) {
        // TODO: Complete logic
    }

    public Level getLevel() {
        return level;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    public String getId() {
        return id;
    }
}
