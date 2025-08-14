package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.color.Color;
import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSendTankColors implements INetworkEvent {
    private static final Logger log = LoggerFactory.getLogger(EventSendTankColors.class);
    private Color color1;
    private Color color2;
    private Color color3;

    public EventSendTankColors() {}

    public EventSendTankColors(Color color1, Color color2, Color color3) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeColor(b, color1);
        NetworkUtils.writeColor(b, color2);
        NetworkUtils.writeColor(b, color3);
    }

    @Override
    public void read(ByteBuf b) {
        color1 = NetworkUtils.readColor(b);
        color2 = NetworkUtils.readColor(b);
        color3 = NetworkUtils.readColor(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        Player player = PlayerManager.getInstance().getPlayer(ctx);
        if (player == null) return;
        log.debug("Received tank colors: {}, {}, {}", color1, color2, color3);
        player.setTankColor(new TankColor(color1, color2, color3));
        log.debug("Set tank color to: {}", player.getName());
    }
}
