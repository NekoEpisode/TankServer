package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;

public class EventTankRemove implements INetworkEvent {
    private int tankNetworkId;
    private boolean destroyAnimation;

    public EventTankRemove() {}

    public EventTankRemove(int tankNetworkId, boolean destroyAnimation) {
        this.tankNetworkId = tankNetworkId;
        this.destroyAnimation = destroyAnimation;
    }

    @Override
    public void write(ByteBuf b) {
        b.writeInt(this.tankNetworkId);
        b.writeBoolean(this.destroyAnimation);
    }

    @Override
    public void read(ByteBuf b) {
        this.tankNetworkId = b.readInt();
        this.destroyAnimation = b.readBoolean();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
