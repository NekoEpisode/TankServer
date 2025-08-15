package net.nekoepisode.tankserver;

import net.nekoepisode.tankserver.command.CommandManager;
import net.nekoepisode.tankserver.command.commands.HelpCommand;
import net.nekoepisode.tankserver.command.commands.PlayCommand;
import net.nekoepisode.tankserver.command.commands.TestCommand;
import net.nekoepisode.tankserver.network.NetworkManager;
import net.nekoepisode.tankserver.network.event.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TankServer Main class
 * @version 1.6.e / protocol 59
 * @author NekoEpisode
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Server server = null;

    private static final int port = 8080;

    public static void main(String[] args) throws Exception {
        // register network packets (events)
        NetworkManager.getInstance().registerEvent(0, new EventSendClientDetails());
        NetworkManager.getInstance().registerEvent(2, new EventConnectionSuccess());
        NetworkManager.getInstance().registerEvent(3, new EventKick());
        NetworkManager.getInstance().registerEvent(4, new EventAnnounceConnection());
        NetworkManager.getInstance().registerEvent(5, new EventChat());
        NetworkManager.getInstance().registerEvent(6, new EventPlayerChat());
        NetworkManager.getInstance().registerEvent(8, new EventLoadLevel());
        NetworkManager.getInstance().registerEvent(9, new EventEnterLevel());
        NetworkManager.getInstance().registerEvent(13, new EventLevelExit());
        NetworkManager.getInstance().registerEvent(29, new EventPlayerReady());
        NetworkManager.getInstance().registerEvent(34, new EventUpdateReadyPlayers());
        NetworkManager.getInstance().registerEvent(37, new EventBeginLevelCountDown());
        NetworkManager.getInstance().registerEvent(38, new EventTankUpdate());
        NetworkManager.getInstance().registerEvent(40, new EventTankControllerUpdateC());
        NetworkManager.getInstance().registerEvent(43, new EventTankPlayerCreate());
        NetworkManager.getInstance().registerEvent(44, new EventTankCreate());
        NetworkManager.getInstance().registerEvent(47, new EventAirdropTank());
        NetworkManager.getInstance().registerEvent(49, new EventTankRemove()); // Oh no i wrote wrong id!!! (fixed)
        NetworkManager.getInstance().registerEvent(79, new EventPlaySound());
        NetworkManager.getInstance().registerEvent(80, new EventSendTankColors());
        NetworkManager.getInstance().registerEvent(82, new EventShareLevel());

        // Register commands
        CommandManager.getInstance().registerCommand("help", new HelpCommand());
        CommandManager.getInstance().registerCommand("test", new TestCommand());
        CommandManager.getInstance().registerCommand("play", new PlayCommand());

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null)
                server.close();
        }));

        log.info("Starting server on port {}", port);

        // Start server
        server = new Server(port);
        server.run();
    }
}
