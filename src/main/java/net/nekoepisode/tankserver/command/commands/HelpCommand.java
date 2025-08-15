package net.nekoepisode.tankserver.command.commands;

import net.nekoepisode.tankserver.command.ICommand;
import net.nekoepisode.tankserver.command.CommandManager;
import net.nekoepisode.tankserver.command.CommandSender;

public class HelpCommand implements ICommand {
    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("Available commands:");
            for (String commandName : CommandManager.getInstance().getCommandNames()) {
                commandSender.sendMessage("/" + commandName + " - " + CommandManager.getInstance().getCommand(commandName).getDescription());
            }
            return;
        }

        String commandName = args[0];
        ICommand command = CommandManager.getInstance().getCommand(commandName);
        if (command != null) {
            commandSender.sendMessage("/" + commandName + " - " + command.getDescription());
        } else {
            commandSender.sendMessage("Command not found");
        }
    }

    @Override
    public String getDescription() {
        return "Show command help";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
