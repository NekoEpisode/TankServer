package net.neko.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.neko.game.GameInstance;
import net.neko.game.GameInstanceManager;
import net.neko.game.level.Level;
import net.neko.game.level.share.ShareLevelManager;
import net.neko.game.player.Player;
import net.neko.network.PlayerManager;
import net.neko.network.event.INetworkEvent;
import net.neko.network.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            switch (args[0]) {
                case "/help" -> {
                    player.sendEvent(new EventPlayerChat("Server", "Available commands: /help"));
                }
                case "/test" -> {
                    switch (args[1]) {
                        case "load" -> {
                            player.sendEvent(new EventLoadLevel(
                                    args[2],
                                    3,
                                    false
                            ));
                        }
                        case "enter" -> player.sendEvent(new EventEnterLevel());
                        case "exit" -> player.sendEvent(new EventLevelExit(args[2]));
                    }
                }
                case "/play" -> {
                    Level level = ShareLevelManager.getInstance().getLevel(args[1]);
                    if (level == null) {
                        player.sendEvent(new EventPlayerChat("Server", "Level not found: " + args[2]));
                        return;
                    }

                    GameInstance gameInstance = GameInstanceManager.getInstance().getGameInstance(args[1]);
                    if (gameInstance == null) {
                        player.sendServerMessage("GameInstance not found...Creating new one...");
                        gameInstance = GameInstanceManager.getInstance().addGameInstance(new GameInstance(level));
                    }
                    player.sendServerMessage("Adding you to GameInstance!");
                    gameInstance.addPlayer(player);
                }
                default -> {
                    player.sendEvent(new EventPlayerChat("Server", "Unknown command: " + args[0]));
                }
            }

            log.info("{} performed command: {}", player.getName(), message);
        } else {
            for (Player player : PlayerManager.getInstance().getPlayers()) {
                player.sendEvent(new EventPlayerChat(player.getName(), this.message));
            }
            Player player = PlayerManager.getInstance().getPlayer(ctx);
            log.info("{}: {}", (player != null ? player.getName() : "unknown"), message);
        }
    }
}
