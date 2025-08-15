package net.nekoepisode.tankserver.command.commands;

import net.nekoepisode.tankserver.command.CommandSender;
import net.nekoepisode.tankserver.command.ICommand;
import net.nekoepisode.tankserver.game.GameInstance;
import net.nekoepisode.tankserver.game.GameInstanceManager;
import net.nekoepisode.tankserver.game.level.Level;
import net.nekoepisode.tankserver.game.level.share.ShareLevelManager;
import net.nekoepisode.tankserver.game.player.Player;
import net.nekoepisode.tankserver.game.player.PlayerState;
import net.nekoepisode.tankserver.network.event.events.EventPlayerChat;

public class PlayCommand implements ICommand {
    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage("Usage: /play <level_name>");
            return;
        }

        Player player = (Player) commandSender;
        if (player.getPlayerState() != PlayerState.IN_LOBBY) {
            player.sendEvent(new EventPlayerChat("Server", "You must be in the lobby to play!"));
            return;
        }

        String levelName = args[0];

        Level level = ShareLevelManager.getInstance().getLevel(levelName);
        if (level == null) {
            player.sendEvent(new EventPlayerChat("Server", "Level not found: " + levelName));
            return;
        }

        GameInstance gameInstance = GameInstanceManager.getInstance().getGameInstance(levelName);
        if (gameInstance == null) {
            player.sendMessage("GameInstance not found...Creating new one...");
            gameInstance = GameInstanceManager.getInstance().addGameInstance(new GameInstance(level));
            gameInstance.startLoop();
        }
        player.sendMessage("Adding you to GameInstance!");
        gameInstance.addPlayer(player);
    }

    @Override
    public String getDescription() {
        return "Load a level and create/join a GameInstance";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
