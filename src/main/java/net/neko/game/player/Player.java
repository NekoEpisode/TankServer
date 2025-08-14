package net.neko.game.player;

import net.neko.game.color.Color;
import net.neko.game.color.TankColor;
import net.neko.game.level.Level;
import net.neko.game.tank.PlayerTank;
import net.neko.network.PlayerConnection;
import net.neko.network.event.INetworkEvent;
import net.neko.network.event.events.EventPlayerChat;

import java.util.UUID;

public class Player {
    private final UUID uuid;
    private final String name;
    private final PlayerConnection playerConnection;

    private PlayerTank playerTank = null;
    private Level currentLevel = null;

    private TankColor tankColor = new TankColor(new Color(0, 150, 255), new Color(32, 107, 159), new Color(16, 128, 207)); // Default blue tank

    public Player(UUID uuid, String name, PlayerConnection playerConnection) {
        this.uuid = uuid;
        this.name = name;
        this.playerConnection = playerConnection;
    }

    public void sendEvent(INetworkEvent event) {
        playerConnection.sendEvent(event);
    }

    public void sendServerMessage(String message) {
        sendEvent(new EventPlayerChat("Server", message));
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setTankColor(TankColor tankColor) {
        this.tankColor = tankColor;
    }

    public TankColor getTankColor() {
        return tankColor;
    }

    public PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    public void setPlayerTank(PlayerTank playerTank) {
        this.playerTank = playerTank;
    }

    public PlayerTank getTank() {
        return playerTank;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
