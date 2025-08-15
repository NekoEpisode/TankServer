package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

import java.util.ArrayList;

public class EventUpdateReadyPlayers implements INetworkEvent {
    private String readyPlayers;

    public EventUpdateReadyPlayers() {}

    public EventUpdateReadyPlayers(String readyPlayers) {
        this.readyPlayers = readyPlayers;
    }

    public EventUpdateReadyPlayers(ArrayList<Player> players) {
        StringBuilder s = new StringBuilder();
        for (Player p: players)
            s.append(p.getUuid()).append(",");

        if (players.isEmpty())
            readyPlayers = "";
        else
            readyPlayers = s.substring(0, s.length() - 1);
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.readyPlayers);
    }

    @Override
    public void read(ByteBuf b) {
        this.readyPlayers = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
