package net.neko.game.level.share;

import net.neko.game.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShareLevelManager {
    private static final ShareLevelManager instance = new ShareLevelManager();

    private final Map<String, Level> levelMap = new ConcurrentHashMap<>();
    private final Map<Level, String> reverseLevelMap = new ConcurrentHashMap<>();

    public void addLevel(String name, Level level) {
        levelMap.put(name, level);
        reverseLevelMap.put(level, name);
    }

    public Level getLevel(String name) {
        return levelMap.get(name);
    }

    public String getLevelName(Level level) {
        return reverseLevelMap.get(level);
    }

    public void removeLevel(String name) {
        Level level = levelMap.remove(name);
        reverseLevelMap.remove(level);
    }

    public void removeLevel(Level level) {
        String name = reverseLevelMap.remove(level);
        levelMap.remove(name);
    }

    public static ShareLevelManager getInstance() {
        return instance;
    }
}
