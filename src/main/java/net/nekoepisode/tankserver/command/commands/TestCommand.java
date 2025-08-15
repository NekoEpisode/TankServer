package net.nekoepisode.tankserver.command.commands;

import net.nekoepisode.tankserver.command.ICommand;
import net.nekoepisode.tankserver.command.CommandSender;

public class TestCommand implements ICommand {
    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("No args!");
            return;
        }

        if (args[0].equals("hello")) {
            commandSender.sendMessage("Hello! ^_^");
            return;
        }

        if (args.length > 1) {
            commandSender.sendMessage("You have " + args.length + " args!");
        }
    }

    @Override
    public String getDescription() {
        return "description!";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
