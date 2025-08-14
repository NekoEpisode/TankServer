package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;
import net.neko.network.utils.NetworkUtils;

public class EventPlayerChat implements INetworkEvent {
    private String username;
    private String message;

    public EventPlayerChat() {}

    public EventPlayerChat(String username, String message) {
        this.username = username;
        this.message = message;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.username);
        NetworkUtils.writeString(b, this.message);
    }

    @Override
    public void read(ByteBuf b) {
        this.username = NetworkUtils.readString(b);
        this.message = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
