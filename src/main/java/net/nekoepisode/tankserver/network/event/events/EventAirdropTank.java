package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import net.nekoepisode.tankserver.game.color.Color;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

public class EventAirdropTank extends EventTankCreate {
    private Color color1;
    private Color color2;
    private double height;

    public EventAirdropTank() {}

    /*public EventAirdropTank(Tank tank) {
        super(tank);

        this.color1 = tank.getColor().getColor1();
        this.color2 = tank.getColor().getColor2();
        this.height = tank.getY();
    }*/

    public EventAirdropTank(String type, double posX, double posY, double angle, String team, int id, double drawAge, Color color1, Color color2, double height) {
        super(type, posX, posY, angle, team, id, drawAge);

        this.color1 = color1;
        this.color2 = color2;
        this.height = height;
    }

    @Override
    public void write(ByteBuf b) {
        super.write(b);

        NetworkUtils.writeColor(b, color1);
        NetworkUtils.writeColor(b, color2);
        b.writeDouble(height);
    }

    @Override
    public void read(ByteBuf b) {
        super.read(b);

        this.color1 = NetworkUtils.readColor(b);
        this.color2 = NetworkUtils.readColor(b);
        this.height = b.readDouble();
    }
}
