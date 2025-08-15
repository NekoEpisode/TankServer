package net.nekoepisode.tankserver.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<INetworkEvent> {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, INetworkEvent event) throws Exception {
        try {
            // Validate that the channel is still active before processing
            if (!ctx.channel().isActive()) {
                log.warn("Received event {} on inactive channel", event.getClass().getSimpleName());
                return;
            }

            event.handle(ctx);
        } catch (Exception e) {
            log.error("Error while handling event {} from {}",
                    event.getClass().getSimpleName(), ctx.channel().remoteAddress(), e);

            // Try to find the player and remove them if there's a critical error
            try {
                Player player = PlayerManager.getInstance().getPlayer(ctx);
                if (player != null) {
                    log.warn("Removing player {} due to event handling error", player.getName());
                    player.remove();
                }
            } catch (Exception removalError) {
                log.error("Error during emergency player removal", removalError);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel inactive: {}", ctx.channel().remoteAddress());

        try {
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            if (player != null) {
                log.info("Player {} disconnected", player.getName());

                // Remove the player (this will handle all cleanup)
                player.remove();

                // Notify other players
                notifyPlayersOfDisconnection(player);
            }
        } catch (Exception e) {
            log.error("Error handling channel inactive event", e);
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in channel {}", ctx.channel().remoteAddress(), cause);

        try {
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            if (player != null) {
                log.warn("Removing player {} due to channel exception", player.getName());
                player.remove();
            }
        } catch (Exception e) {
            log.error("Error removing player after exception", e);
        }

        // Close the channel safely
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel registered: {}", ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel unregistered: {}", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    private void notifyPlayersOfDisconnection(Player disconnectedPlayer) {
        try {
            for (Player player : PlayerManager.getInstance().getPlayers()) {
                if (!player.equals(disconnectedPlayer)) {
                    try {
                        player.sendMessage(disconnectedPlayer.getName() + " left the game.");
                    } catch (Exception e) {
                        log.error("Error notifying player {} of disconnection", player.getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error notifying players of disconnection", e);
        }
    }
}