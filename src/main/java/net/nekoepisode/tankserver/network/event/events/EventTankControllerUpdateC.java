package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.game.player.PlayerState;
import net.nekoepisode.tankserver.game.tank.PlayerTank;
import net.nekoepisode.tankserver.game.tank.Tank;
import net.nekoepisode.tankserver.game.tank.TankManager;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventTankControllerUpdateC implements INetworkEvent {
    private static final Logger log = LoggerFactory.getLogger(EventTankControllerUpdateC.class);
    private int tankNetworkId;
    private double posX;
    private double posY;
    private double vX;
    private double vY;
    private double angle;
    private double mX;
    private double mY;
    private boolean action1;
    private boolean action2;
    private final boolean[] quickActions = new boolean[5];
    private double time;

    public EventTankControllerUpdateC() {}

    @Override
    public void write(ByteBuf b) {
        b.writeInt(this.tankNetworkId);
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.vX);
        b.writeDouble(this.vY);
        b.writeDouble(this.angle);
        b.writeDouble(this.mX);
        b.writeDouble(this.mY);
        b.writeBoolean(this.action1);
        b.writeBoolean(this.action2);
        for (boolean quickAction : this.quickActions)
        {
            b.writeBoolean(quickAction);
        }
        b.writeDouble(this.time);
    }

    @Override
    public void read(ByteBuf b) {
        this.tankNetworkId = b.readInt();
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.vX = b.readDouble();
        this.vY = b.readDouble();
        this.angle = b.readDouble();
        this.mX = b.readDouble();
        this.mY = b.readDouble();
        this.action1 = b.readBoolean();
        this.action2 = b.readBoolean();
        for (int i = 0; i < this.quickActions.length; i++)
        {
            this.quickActions[i] = b.readBoolean();
        }
        this.time = b.readDouble();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        Player player = PlayerManager.getInstance().getPlayer(ctx);
        if (player == null) return;

        if (player.getPlayerState() != PlayerState.INGAME) {
            player.setPlayerState(PlayerState.INGAME);
        }

        Tank tank = TankManager.getInstance().getTank(tankNetworkId);
        if (!(tank instanceof PlayerTank playerTank)) return;

        playerTank.setX(posX);
        playerTank.setY(posY);
        playerTank.setVX(vX);
        playerTank.setVY(vY);
        playerTank.setAngle(angle);
        playerTank.setMX(mX);
        playerTank.setMY(mY);
        playerTank.setAction1(action1);
        playerTank.setAction2(action2);
        playerTank.setQuickActions(quickActions);
        playerTank.setTime(time);

        List<Player> players = new ArrayList<>(PlayerManager.getInstance().getPlayers());
        for (Player recipient : players) {
            try {
                if (recipient.getPlayerConnection().ctx().channel().isActive()) {
                    recipient.sendEvent(new EventTankUpdate(playerTank));
                }
            } catch (Exception e) {
                log.error("发送坦克更新失败", e);
            }
        }
    }
}
