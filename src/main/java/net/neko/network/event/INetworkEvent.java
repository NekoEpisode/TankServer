package net.neko.network.event;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface INetworkEvent extends IEvent {
	void write(ByteBuf b);
	
	void read(ByteBuf b);
	
	void handle(ChannelHandlerContext ctx);
}
