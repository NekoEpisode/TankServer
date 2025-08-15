package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.command.ICommand;
import net.nekoepisode.tankserver.command.CommandManager;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.network.PlayerManager;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class EventChat implements INetworkEvent {
    private static final Logger log = LoggerFactory.getLogger(EventChat.class);
    private String message;

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.message);
    }

    @Override
    public void read(ByteBuf b) {
        this.message = NetworkUtils.readString(b);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (message.length() > 1000) {
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            if (player != null) {
                player.sendEvent(new EventPlayerChat("Server", "The message you wrote is too long! (max: 1000, got: " + message.length() + ")"));
                return;
            }
        }

        if (message.startsWith("/")) {
            String[] args = message.split(" ");
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            if (player == null) {
                return;
            }
            player.sendEvent(new EventPlayerChat(player.getName(), ">> " + message));

            String commandName = args[0];
            commandName = commandName.substring(1); // Remove first "/"

            ICommand command = CommandManager.getInstance().getCommand(commandName);

            if (command == null) {
                player.sendEvent(new EventPlayerChat("Server", "Command not found!"));
                return;
            }

            // Skip first arg (command name)
            args = Arrays.copyOfRange(args, 1, args.length);

            command.execute(player, args);

            log.info("{} performed command: {}", player.getName(), message);
        } else {
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            for (Player player1 : PlayerManager.getInstance().getPlayers()) {
                player1.sendEvent(new EventPlayerChat((player != null ? player.getName() : "unknown"), this.message));
            }
            log.info("{}: {}", (player != null ? player.getName() : "unknown"), message);
        }
    }
}
