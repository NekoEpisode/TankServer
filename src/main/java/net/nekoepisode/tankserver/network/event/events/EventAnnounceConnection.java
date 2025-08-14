package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

import java.util.Objects;
import java.util.UUID;

public class EventAnnounceConnection implements INetworkEvent {
    public String name;
    public UUID clientIdTarget;
    public boolean joined;
    public boolean isBot;

    public EventAnnounceConnection() {}

    public EventAnnounceConnection(String name, UUID clientIdTarget, boolean joined, boolean isBot) {
        this.name = name;
        this.clientIdTarget = clientIdTarget;
        this.joined = joined;
        this.isBot = isBot;
    }

    public EventAnnounceConnection(Player player, boolean joined) {
        this.name = player.getName();
        this.clientIdTarget = player.getUuid();
        this.joined = joined;
        this.isBot = false;
    }

    @Override
    public void read(ByteBuf b) {
        this.joined = b.readBoolean();
        this.clientIdTarget = UUID.fromString(Objects.requireNonNull(NetworkUtils.readString(b)));
        this.name = NetworkUtils.readString(b);
        this.isBot = b.readBoolean();
    }

    @Override
    public void write(ByteBuf b) {
        b.writeBoolean(this.joined);
        NetworkUtils.writeString(b, this.clientIdTarget.toString());
        NetworkUtils.writeString(b, this.name);
        b.writeBoolean(this.isBot);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
