package net.neko.network;

import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;

public record PlayerConnection(ChannelHandlerContext ctx) {

    public void sendEvent(INetworkEvent event) {
        ctx.writeAndFlush(event);
    }
}
