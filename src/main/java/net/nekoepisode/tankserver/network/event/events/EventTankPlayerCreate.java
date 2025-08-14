package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.color.Color;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.game.team.Team;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

import java.util.Objects;
import java.util.UUID;

public class EventTankPlayerCreate implements INetworkEvent {
    private UUID targetClientId;
    private String username;
    private double posX;
    private double posY;
    private double angle;
    private String teamName;
    private Color color1;
    private Color color2;
    private Color color3;
    private int networkId;
    private double drawAge;

    public EventTankPlayerCreate() {}

    public EventTankPlayerCreate(Player player, double posX, double posY, double angle, Team team, int networkId, double drawAge) {
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
        this.teamName = team.name;
        this.networkId = networkId;
        this.drawAge = drawAge;
        this.targetClientId = player.getUuid();
        this.username = player.getName();
        this.color1 = player.getTankColor().getColor1();
        this.color2 = player.getTankColor().getColor2();
        this.color3 = player.getTankColor().getColor3();
    }

    @Override
    public void write(ByteBuf b)
    {
        NetworkUtils.writeString(b, this.targetClientId.toString());
        NetworkUtils.writeString(b, this.username);
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.angle);
        NetworkUtils.writeString(b, this.teamName);
        b.writeInt(this.networkId);

        NetworkUtils.writeColor(b, this.color1);
        NetworkUtils.writeColor(b, this.color2);
        NetworkUtils.writeColor(b, this.color3);

        b.writeDouble(this.drawAge);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.targetClientId = UUID.fromString(Objects.requireNonNull(NetworkUtils.readString(b)));
        this.username = NetworkUtils.readString(b);
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.angle = b.readDouble();
        this.teamName = NetworkUtils.readString(b);
        this.networkId = b.readInt();

        this.color1 = NetworkUtils.readColor(b);
        this.color2 = NetworkUtils.readColor(b);
        this.color3 = NetworkUtils.readColor(b);

        this.drawAge = b.readDouble();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public void setColor3(Color color3) {
        this.color3 = color3;
    }
}
