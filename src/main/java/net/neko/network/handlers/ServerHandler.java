package net.neko.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.neko.game.player.Player;
import net.neko.network.PlayerManager;
import net.neko.network.event.INetworkEvent;
import net.neko.network.event.events.EventAnnounceConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<INetworkEvent> {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, INetworkEvent event) throws Exception {
        try {
            event.handle(ctx);
        } catch (Exception e) {
            log.error("Error while handling event", e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Player player = PlayerManager.getInstance().getPlayer(ctx);
        if (player != null) {
            PlayerManager.getInstance().removePlayer(player);
            for (Player player1 : PlayerManager.getInstance().getPlayers()) {
                player1.sendEvent(new EventAnnounceConnection(player, false));
            }
            log.info("Player {} disconnected", player.getName());
        }
    }
}