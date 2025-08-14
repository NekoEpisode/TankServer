package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.PlayerConnection;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

public class EventSendClientDetails implements INetworkEvent {
    private static final Logger log = LoggerFactory.getLogger(EventSendClientDetails.class);
    public int version;
    public UUID clientID;
    public String username;

    public EventSendClientDetails() {}

    public EventSendClientDetails(int version, UUID clientID, String username) {
        this.version = version;
        this.clientID = clientID;
        this.username = username;
    }

    @Override
    public void write(ByteBuf b) {
        b.writeInt(this.version);
        NetworkUtils.writeString(b, clientID.toString());
        NetworkUtils.writeString(b, username);
    }

    @Override
    public void read(ByteBuf b) {
        this.version = b.readInt();
        this.clientID = UUID.fromString(Objects.requireNonNull(NetworkUtils.readString(b)));
        this.username = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        log.debug("Received client details: {} {} {}", version, clientID, username);
        if (version != 59) {
            ctx.writeAndFlush(new EventKick("You need to use game protocol version 59 to connect to this server."));
            ctx.close();
            return;
        }
        Player player = new Player(clientID, username, new PlayerConnection(ctx));
        PlayerManager.getInstance().addPlayer(player.getUuid(), player);
        log.debug("Added player: {}", player.getName());
        player.sendEvent(new EventConnectionSuccess());
        player.sendEvent(new EventPlayerChat("Server", "Welcome to the server, " + player.getName() + "!"));

        for (Player player1 : PlayerManager.getInstance().getPlayers()) {
            player1.sendEvent(new EventAnnounceConnection(player, true));
            if (!player1.equals(player))
                player.sendEvent(new EventAnnounceConnection(player1, true));
        }
    }
}
