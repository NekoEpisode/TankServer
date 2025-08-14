package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;
import net.neko.network.utils.NetworkUtils;

public class EventLevelExit implements INetworkEvent {
    private String winningTeam;

    public EventLevelExit() {}

    public EventLevelExit(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.winningTeam);
    }

    @Override
    public void read(ByteBuf b) {
        this.winningTeam = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
