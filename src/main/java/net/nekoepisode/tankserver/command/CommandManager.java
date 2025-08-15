package net.nekoepisode.tankserver.command;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private static final CommandManager instance = new CommandManager();

    private final Map<String, ICommand> commandMap = new ConcurrentHashMap<>(); // Command name -> Command executor
    private final Map<ICommand, String> reverseCommandMap = new ConcurrentHashMap<>(); // Command executor -> Command name

    public void registerCommand(String commandName, ICommand command) {
        commandMap.put(commandName, command);
        reverseCommandMap.put(command, commandName);
    }

    public void unregisterCommand(String commandName) {
        ICommand command = commandMap.remove(commandName);
        if (command != null) {
            reverseCommandMap.remove(command);
        }
    }

    public ICommand getCommand(String commandName) {
        return commandMap.get(commandName);
    }

    public String getCommandName(ICommand command) {
        return reverseCommandMap.get(command);
    }

    public int getCommandCount() {
        return commandMap.size();
    }

    public List<String> getCommandNames() {
        return commandMap.keySet().stream().toList();
    }

    public List<ICommand> getCommands() {
        return commandMap.values().stream().toList();
    }

    public void execute(String commandName, CommandSender commandSender, String[] args) {
        ICommand command = getCommand(commandName);
        if (command != null) {
            command.execute(commandSender, args);
        }
    }

    public static CommandManager getInstance() {
        synchronized (CommandManager.class) {
            return instance;
        }
    }
}
