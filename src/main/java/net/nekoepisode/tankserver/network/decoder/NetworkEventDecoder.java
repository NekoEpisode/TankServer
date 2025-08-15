package net.nekoepisode.tankserver.network.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.NetworkManager;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.event.events.EventPlayerChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NetworkEventDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(NetworkEventDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 8) return;

        in.markReaderIndex();

        // read length
        int length = in.readInt();
        int networkId = in.readInt();

        // check if data is complete
        if (in.readableBytes() < length - 4) {
            in.resetReaderIndex(); // data is not complete
            return;
        }

        log.debug("Received packet with network id {}", networkId);

        // get event with id
        INetworkEvent event = NetworkManager.getInstance().getEvent(networkId);
        if (event == null) {
            log.warn("Unknown networkId: {}", networkId);
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            if (player != null) {
                player.sendEvent(new EventPlayerChat("Server", "Not supported networkId: " + networkId));
            }
            return;
        }

        try {
            event.read(in.readSlice(length - 4)); // read data
        } catch (Exception e) {
            log.error("Error reading event data: {}", e.getMessage());
            return;
        }

        out.add(event);
    }
}
