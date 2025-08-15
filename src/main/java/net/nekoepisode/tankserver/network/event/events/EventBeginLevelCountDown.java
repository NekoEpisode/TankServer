package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;

public class EventBeginLevelCountDown implements INetworkEvent {
    public EventBeginLevelCountDown() {}

    @Override
    public void write(ByteBuf b) {}

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
