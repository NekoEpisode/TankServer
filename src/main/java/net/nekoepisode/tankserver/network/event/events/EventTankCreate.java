package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

public class EventTankCreate implements INetworkEvent {
    private String type;
    private double posX;
    private double posY;
    private double angle;
    private String team;
    private int id;
    private double drawAge;

    public EventTankCreate() {}

    /*public EventTankCreate(Tank tank) {
        this.type = tank.getType();
        this.posX = tank.getX();
        this.posY = tank.getY();
        this.angle = tank.getAngle();
        this.team = tank.getTeam().toString();
        this.id = tank.getID();
        this.drawAge = tank.getDrawAge();
    }*/

    public EventTankCreate(String type, double posX, double posY, double angle, String team, int id, double drawAge) {
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
        this.team = team;
        this.id = id;
        this.drawAge = drawAge;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.type);
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.angle);
        NetworkUtils.writeString(b, this.team);
        b.writeInt(this.id);
        b.writeDouble(this.drawAge);
    }

    @Override
    public void read(ByteBuf b) {
        this.type = NetworkUtils.readString(b);
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.angle = b.readDouble();
        this.team = NetworkUtils.readString(b);
        this.id = b.readInt();
        this.drawAge = b.readDouble();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
