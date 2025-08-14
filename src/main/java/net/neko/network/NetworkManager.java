package net.neko.network;

import net.neko.network.event.INetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {
    private static final NetworkManager instance = new NetworkManager();
    private static final Logger log = LoggerFactory.getLogger(NetworkManager.class);

    private final Map<Integer, INetworkEvent> eventMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends INetworkEvent>, Integer> eventClassMap = new ConcurrentHashMap<>();

    public void registerEvent(int id, INetworkEvent event) {
        eventMap.put(id, event);
        eventClassMap.put(event.getClass(), id);
    }

    public INetworkEvent getEvent(int id) {
        INetworkEvent template = eventMap.get(id);
        if (template == null) return null;

        try {
            return template.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Failed to create event instance: {}", e.getMessage());
            return null;
        }
    }

    public int getNetworkId(Class<? extends INetworkEvent> eventClass) {
        return eventClassMap.getOrDefault(eventClass, -1);
    }

    public int getRegisteredEventCount() {
        return eventMap.size();
    }

    public static NetworkManager getInstance() {
        synchronized (NetworkManager.class) {
            return instance;
        }
    }
}