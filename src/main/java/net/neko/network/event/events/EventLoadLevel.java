package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.network.event.INetworkEvent;
import net.neko.network.utils.NetworkUtils;

public class EventLoadLevel implements INetworkEvent {
    private String levelString;
    public double startTime;
    public boolean disableFriendlyFire;

    public EventLoadLevel() {}

    public EventLoadLevel(String levelString, double startTime, boolean disableFriendlyFire) {
        this.levelString = levelString;
        this.startTime = startTime;
        this.disableFriendlyFire = disableFriendlyFire;
    }

    @Override
    public void read(ByteBuf b) {
        this.levelString = NetworkUtils.readString(b);
        this.startTime = b.readDouble();
        this.disableFriendlyFire = b.readBoolean();
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.levelString);
        b.writeDouble(this.startTime);
        b.writeBoolean(this.disableFriendlyFire);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
