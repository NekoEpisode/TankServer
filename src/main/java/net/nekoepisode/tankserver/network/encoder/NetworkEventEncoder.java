package net.nekoepisode.tankserver.network.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.nekoepisode.tankserver.network.NetworkManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkEventEncoder extends MessageToByteEncoder<INetworkEvent> {
    private static final Logger log = LoggerFactory.getLogger(NetworkEventEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, INetworkEvent event, ByteBuf out) throws Exception {
        int networkId = getNetworkId(event);

        if (networkId == -1) {
            log.error("Event not registered: {}", event.getClass().getSimpleName());
            return;
        }

        ByteBuf tempBuffer = ctx.alloc().buffer();
        try {
            event.write(tempBuffer);

            int dataLength = tempBuffer.readableBytes();

            out.writeInt(dataLength + 4);
            out.writeInt(networkId);
            out.writeBytes(tempBuffer);

            log.debug("Sent packet with network id {}, data length: {}", networkId, dataLength);
        } finally {
            tempBuffer.release();
        }
    }

    private int getNetworkId(INetworkEvent event) {
        return NetworkManager.getInstance().getNetworkId(event.getClass());
    }
}