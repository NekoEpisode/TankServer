package net.nekoepisode.tankserver.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameInstanceManager {
    private static final GameInstanceManager instance = new GameInstanceManager();

    private final Map<String, GameInstance> gameInstanceMap = new ConcurrentHashMap<>();
    private final Map<GameInstance, String> reverseGameInstanceMap = new ConcurrentHashMap<>();

    public GameInstance addGameInstance(GameInstance gameInstance) {
        gameInstanceMap.put(gameInstance.getId(), gameInstance);
        reverseGameInstanceMap.put(gameInstance, gameInstance.getId());
        return gameInstance;
    }

    public GameInstance getGameInstance(String id) {
        return gameInstanceMap.get(id);
    }

    public String getGameInstanceId(GameInstance gameInstance) {
        return reverseGameInstanceMap.get(gameInstance);
    }

    public void removeGameInstance(GameInstance gameInstance) {
        gameInstanceMap.remove(gameInstance.getId());
        reverseGameInstanceMap.remove(gameInstance);
    }

    public void removeGameInstance(String id) {
        GameInstance gameInstance = gameInstanceMap.get(id);
        if (gameInstance != null) {
            removeGameInstance(gameInstance);
        }
    }

    public int getGameInstanceCount() {
        return gameInstanceMap.size();
    }

    public static GameInstanceManager getInstance() {
        synchronized (GameInstanceManager.class) {
            return instance;
        }
    }
}
