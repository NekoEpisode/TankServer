package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

public class EventKick implements INetworkEvent {
    private String reason;

    public EventKick() {}

    public EventKick(String reason) {
        this.reason = reason;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.reason);
    }

    @Override
    public void read(ByteBuf b) {
        this.reason = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
