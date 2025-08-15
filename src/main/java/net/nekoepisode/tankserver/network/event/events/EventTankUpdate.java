package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.tank.Tank;
import net.nekoepisode.tankserver.game.tank.TankManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTankUpdate implements INetworkEvent {
    private static final Logger log = LoggerFactory.getLogger(EventTankUpdate.class);
    private int tankNetworkId;
    private double posX;
    private double posY;
    private double vX;
    private double vY;
    private double angle;
    private double pitch;

    public EventTankUpdate() {}

    public EventTankUpdate(Tank t) {
        this.tankNetworkId = TankManager.getInstance().getNetworkId(t);
        this.posX = t.getX();
        this.posY = t.getY();
        this.vX = t.getVX();
        this.vY = t.getVY();
        this.angle = t.getAngle();
        this.pitch = t.getPitch();
        System.out.println("EventTankUpdate: " + this.tankNetworkId + " " + this.posX + " " + this.posY + " " + this.vX + " " + this.vY + " " + this.angle + " " + this.pitch);
    }

    @Override
    public void write(ByteBuf b) {
        b.writeInt(this.tankNetworkId);
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.vX);
        b.writeDouble(this.vY);
        b.writeDouble(this.angle);
        b.writeDouble(this.pitch);
    }

    @Override
    public void read(ByteBuf b) {
        this.tankNetworkId = b.readInt();
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.vX = b.readDouble();
        this.vY = b.readDouble();
        this.angle = b.readDouble();
        this.pitch = b.readDouble();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
