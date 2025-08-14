package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;

public class EventConnectionSuccess implements INetworkEvent {
    public EventConnectionSuccess() {}

    @Override
    public void write(ByteBuf b) {}

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
