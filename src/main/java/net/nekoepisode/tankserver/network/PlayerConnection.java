package net.nekoepisode.tankserver.network;

import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;

public record PlayerConnection(ChannelHandlerContext ctx) {

    public void sendEvent(INetworkEvent event) {
        ctx.writeAndFlush(event);
    }
}
