package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.game.player.PlayerState;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;

public class EventPlayerReady implements INetworkEvent {
    public EventPlayerReady() {}

    @Override
    public void write(ByteBuf b) {}

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void handle(ChannelHandlerContext ctx) {
        Player player = PlayerManager.getInstance().getPlayer(ctx);
        if (player == null) return;
        ctx.writeAndFlush(new EventUpdateReadyPlayers(player.getUuid().toString()));
        ctx.writeAndFlush(new EventBeginLevelCountDown());
        player.setPlayerState(PlayerState.INGAME_READY_COUNTING);
    }
}
