package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;

public class EventEnterLevel implements INetworkEvent {
    public EventEnterLevel() {}

    @Override
    public void write(ByteBuf b) {}

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
