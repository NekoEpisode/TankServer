package net.neko.network;

import io.netty.channel.ChannelHandlerContext;
import net.neko.game.player.Player;
import net.neko.network.event.INetworkEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static final PlayerManager instance = new PlayerManager();

    private final Map<UUID, Player> playerMap;
    private final Map<ChannelHandlerContext, Player> ctxMap;

    public PlayerManager() {
        this.playerMap = new ConcurrentHashMap<>();
        this.ctxMap = new ConcurrentHashMap<>();
    }

    public void addPlayer(UUID uuid, Player player) {
        this.playerMap.put(uuid, player);
        this.ctxMap.put(player.getPlayerConnection().ctx(), player);
    }

    public void removePlayer(UUID uuid) {
        Player removed = this.playerMap.remove(uuid);
        if (removed != null) {
            this.ctxMap.remove(removed.getPlayerConnection().ctx());
        }
    }

    public void removePlayer(Player player) {
        this.removePlayer(player.getUuid());
    }

    public Player getPlayer(UUID uuid) {
        return this.playerMap.get(uuid);
    }

    public Player getPlayer(ChannelHandlerContext ctx) {
        return this.ctxMap.get(ctx);
    }

    public List<Player> getPlayers() {
        return this.playerMap.values().stream().toList();
    }

    public void broadcastEvent(INetworkEvent event) {
        this.playerMap.values().forEach(player -> player.sendEvent(event));
    }

    public static PlayerManager getInstance() {
        synchronized (PlayerManager.class) {
            return instance;
        }
    }
}
