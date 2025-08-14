package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.game.level.Level;
import net.neko.game.level.share.ShareLevelManager;
import net.neko.game.player.Player;
import net.neko.network.PlayerManager;
import net.neko.network.event.INetworkEvent;
import net.neko.network.utils.NetworkUtils;

public class EventShareLevel implements INetworkEvent {
    private String level;
    private String name;
    private String username = "";

    public EventShareLevel() {}

    public EventShareLevel(String level, String name) {
        this.level = level;
        this.name = name;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.level);
        NetworkUtils.writeString(b, this.name);
        NetworkUtils.writeString(b, this.username);
    }

    @Override
    public void read(ByteBuf b) {
        this.level = NetworkUtils.readString(b);
        this.name = NetworkUtils.readString(b);
        this.username = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        Level level1 = new Level(name, level);
        ShareLevelManager.getInstance().addLevel(name, level1);
        for (Player player : PlayerManager.getInstance().getPlayers()) {
            player.sendEvent(new EventPlayerChat("Server", player.getName() + " shared a new level: \"" + name + "\", use \"/play " + name + "\" to play it."));
        }
    }
}
