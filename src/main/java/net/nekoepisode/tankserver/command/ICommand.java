package net.nekoepisode.tankserver.command;

public interface ICommand {
    /**
     * Execute command
     *
     * @param commandSender Command sender
     * @param args Command arguments
     */
    void execute(CommandSender commandSender, String[] args);

    /**
     * Get command description
     *
     * @return command description
     */
    String getDescription();

    /**
     * Is command only for player?
     * (Currently not used)
     *
     * @return true if command is only for player, false otherwise
     */
    boolean isPlayerOnly();
}
