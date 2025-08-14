package net.nekoepisode.tankserver.game.level;

import net.nekoepisode.tankserver.game.color.Color;
import net.nekoepisode.tankserver.game.team.Team;
import net.nekoepisode.tankserver.game.item.Item;
import net.nekoepisode.tankserver.game.tank.ShopTankBuild;

import java.util.*;

public class Level {
    private final String levelString;
    private final String name;

    // Parsed level data
    private final LevelSettings settings;
    private final List<PlayerSpawn> playerSpawns;
    private final List<TankSpawn> tankSpawns;
    private final List<ObstacleSpawn> obstacleSpawns;
    private final List<Team> teams;
    private final Map<String, Team> teamsMap;

    // Shop and economy
    private int startingCoins = 0;
    private final List<Item.ShopItem> shop;
    private final List<Item.ItemStack<?>> startingItems;
    private final List<ShopTankBuild> playerBuilds;

    // Advanced features
    private boolean synchronizeMusic = false;
    private int beatBlocks = 0;
    private final boolean disableFriendlyFire = false;

    // Constants
    public static final double TILE_SIZE = 50.0;

    public Level(String name, String levelString) {
        this.name = name;
        this.levelString = levelString != null ? levelString.replaceAll("\u0000", "") : "";
        this.settings = new LevelSettings();
        this.playerSpawns = new ArrayList<>();
        this.tankSpawns = new ArrayList<>();
        this.obstacleSpawns = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamsMap = new HashMap<>();
        this.shop = new ArrayList<>();
        this.startingItems = new ArrayList<>();
        this.playerBuilds = new ArrayList<>();

        parseLevelString();
    }

    private void parseLevelString() {
        if (levelString == null || levelString.isEmpty()) {
            initializeDefaults();
            return;
        }

        try {
            // Split into different sections
            String[] blocks = levelString.split("(?=(level|items|shop|coins|tanks|builds)\n)");

            for (String block : blocks) {
                if (block.startsWith("items\n")) {
                    parseItems(block.substring("items\n".length()));
                } else if (block.startsWith("shop\n")) {
                    parseShop(block.substring("shop\n".length()));
                } else if (block.startsWith("coins\n")) {
                    parseCoins(block.substring("coins\n".length()));
                } else if (block.startsWith("tanks\n")) {
                    parseCustomTanks(block.substring("tanks\n".length()));
                } else if (block.startsWith("builds\n")) {
                    parseBuilds(block.substring("builds\n".length()));
                } else {
                    parseLevelData(block);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing level string: " + e.getMessage());
            initializeDefaults();
        }

        // Ensure we have at least default values
        if (teams.isEmpty()) {
            initializeDefaultTeams();
        }
        if (playerBuilds.isEmpty()) {
            playerBuilds.add(new ShopTankBuild());
        }
    }

    private void parseItems(String itemsData) {
        List<String> objects = getJsonObjects(itemsData);
        for (String obj : objects) {
            startingItems.add(Item.ItemStack.fromString(null, obj));
        }
    }

    private void parseShop(String shopData) {
        List<String> objects = getJsonObjects(shopData);
        for (String obj : objects) {
            shop.add(Item.ShopItem.fromString(obj));
        }
    }

    private void parseCoins(String coinsData) {
        try {
            startingCoins = (int) Double.parseDouble(coinsData.trim());
        } catch (NumberFormatException e) {
            startingCoins = 0;
        }
    }

    private void parseCustomTanks(String tanksData) {
        // TODO: Parse custom tanks
    }

    private void parseBuilds(String buildsData) {
        List<String> objects = getJsonObjects(buildsData);
        for (String obj : objects) {
            ShopTankBuild build = ShopTankBuild.fromString(obj);
            build.enableTertiaryColor = true;
            playerBuilds.add(build);
        }
    }

    private void parseLevelData(String levelData) {
        if (levelData.startsWith("level\n")) {
            levelData = levelData.substring("level\n".length());
        }

        if (!levelData.contains("{") || !levelData.contains("}")) {
            return;
        }

        String content = levelData.substring(levelData.indexOf('{') + 1, levelData.indexOf('}'));
        String[] sections = content.split("\\|");

        if (sections.length > 0) parseScreenSection(sections[0]);
        if (sections.length > 1) parseObstaclesSection(sections[1]);
        if (sections.length > 2) parseTanksSection(sections[2]);
        if (sections.length > 3) parseTeamsSection(sections[3]);
    }

    private void parseScreenSection(String screenData) {
        String[] parts = screenData.split(",");

        // Check if editable
        if (parts[0].startsWith("*")) {
            settings.editable = false;
            parts[0] = parts[0].substring(1);
        }

        // Parse size
        try {
            settings.sizeX = (int) Double.parseDouble(parts[0]);
            settings.sizeY = (int) Double.parseDouble(parts[1]);
        } catch (Exception e) {
            settings.sizeX = 28;
            settings.sizeY = 18;
        }

        // Parse background color
        if (parts.length >= 5) {
            try {
                settings.backgroundColor.set(
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4])
                );

                // Parse color variation
                if (parts.length >= 8) {
                    int colorVarR = Math.min(255 - (int) settings.backgroundColor.red, (int) Double.parseDouble(parts[5]));
                    int colorVarG = Math.min(255 - (int) settings.backgroundColor.green, (int) Double.parseDouble(parts[6]));
                    int colorVarB = Math.min(255 - (int) settings.backgroundColor.blue, (int) Double.parseDouble(parts[7]));
                    settings.backgroundVariation.set(colorVarR, colorVarG, colorVarB);
                }
            } catch (Exception e) {
                // Keep default colors
            }
        }

        // Parse timer
        if (parts.length >= 9) {
            try {
                int length = (int) Double.parseDouble(parts[8]) * 100;
                if (length > 0) {
                    settings.timed = true;
                    settings.timer = length;
                }
            } catch (Exception e) {
                // Keep default timer settings
            }
        }

        // Parse lighting
        if (parts.length >= 11) {
            try {
                settings.lightIntensity = Double.parseDouble(parts[9]) / 100.0;
                settings.shadowIntensity = Double.parseDouble(parts[10]) / 100.0;
            } catch (Exception e) {
                // Keep default lighting
            }
        }
    }

    private void parseObstaclesSection(String obstaclesData) {
        if (obstaclesData.isEmpty()) return;

        String[] obstacleEntries = obstaclesData.split(",");

        for (String entry : obstacleEntries) {
            if (entry.trim().isEmpty()) continue;

            try {
                String[] parts = entry.split("-");
                if (parts.length < 2) continue;

                // Parse X range
                String[] xRange = parts[0].split("\\.\\.\\.");
                double startX = Double.parseDouble(xRange[0]);
                double endX = xRange.length > 1 ? Double.parseDouble(xRange[1]) : startX;

                // Parse Y range
                String[] yRange = parts[1].split("\\.\\.\\.");
                double startY = Double.parseDouble(yRange[0]);
                double endY = yRange.length > 1 ? Double.parseDouble(yRange[1]) : startY;

                String type = parts.length >= 3 ? parts[2] : "normal";
                String metadata = parts.length >= 4 ? parts[3] : null;

                // Create obstacles for each position in range
                for (double x = startX; x <= endX; x++) {
                    for (double y = startY; y <= endY; y++) {
                        obstacleSpawns.add(new ObstacleSpawn(x, y, type, metadata));

                        // Handle special obstacle types
                        if ("beat".equals(type) || type.contains("beat")) {
                            synchronizeMusic = true;
                            beatBlocks |= 1; // Simple beat frequency
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error parsing obstacle entry: " + entry);
            }
        }
    }

    private void parseTanksSection(String tanksData) {
        if (tanksData.isEmpty()) return;

        String[] tankEntries = tanksData.split(",");

        for (String entry : tankEntries) {
            if (entry.trim().isEmpty()) continue;

            try {
                String[] parts = entry.split("-");
                if (parts.length < 3) continue;

                double x = TILE_SIZE * (0.5 + Double.parseDouble(parts[0]));
                double y = TILE_SIZE * (0.5 + Double.parseDouble(parts[1]));
                String type = parts[2].toLowerCase();
                double angle = 0;

                // Parse angle
                if (parts.length >= 4) {
                    try {
                        angle = Math.PI / 2 * Double.parseDouble(parts[3]);
                    } catch (NumberFormatException e) {
                        angle = 0;
                    }
                }

                // Parse team
                Team team = getDefaultEnemyTeam();
                if (parts.length >= 5) {
                    team = teamsMap.getOrDefault(parts[4], team);
                }

                // Build metadata from remaining parts
                StringBuilder metadata = new StringBuilder();
                for (int i = 3; i < parts.length; i++) {
                    metadata.append(parts[i]);
                    if (i < parts.length - 1) metadata.append("-");
                }

                // Handle player spawns
                if ("player".equals(type)) {
                    Team playerTeam = team;
                    if (team == getDefaultEnemyTeam()) {
                        playerTeam = getDefaultPlayerTeam();
                    }
                    playerSpawns.add(new PlayerSpawn(x, y, angle, playerTeam));
                } else {
                    // Regular tank spawn
                    tankSpawns.add(new TankSpawn(x, y, type, angle, team, metadata.toString()));
                }
            } catch (Exception e) {
                System.err.println("Error parsing tank entry: " + entry);
            }
        }
    }

    private void parseTeamsSection(String teamsData) {
        String[] teamEntries = teamsData.split(",");

        for (String entry : teamEntries) {
            if (entry.trim().isEmpty()) continue;

            try {
                String[] parts = entry.split("-");
                if (parts.length == 0) continue;

                String name = parts[0];
                Team team = getTeam(parts, name);

                if (disableFriendlyFire) {
                    team.friendlyFire = false;
                }

                teams.add(team);
                teamsMap.put(name, team);
            } catch (Exception e) {
                System.err.println("Error parsing team entry: " + entry);
            }
        }
    }

    private static Team getTeam(String[] parts, String name) {
        boolean friendlyFire = parts.length >= 2 && Boolean.parseBoolean(parts[1]);

        Team team;
        if (parts.length >= 5) {
            // Team with custom color
            team = new Team(name, friendlyFire,
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Double.parseDouble(parts[4])
            );
        } else {
            team = new Team(name, friendlyFire);
        }
        return team;
    }

    private void initializeDefaults() {
        settings.sizeX = 28;
        settings.sizeY = 18;
        initializeDefaultTeams();
    }

    private void initializeDefaultTeams() {
        Team playerTeam = new Team("ally", false);
        Team enemyTeam = new Team("enemy", false);

        if (disableFriendlyFire) {
            playerTeam.friendlyFire = false;
            enemyTeam.friendlyFire = false;
        }

        teams.add(playerTeam);
        teams.add(enemyTeam);
        teamsMap.put("ally", playerTeam);
        teamsMap.put("enemy", enemyTeam);
    }

    private Team getDefaultPlayerTeam() {
        return teamsMap.getOrDefault("ally", new Team("ally", false));
    }

    private Team getDefaultEnemyTeam() {
        return teamsMap.getOrDefault("enemy", new Team("enemy", false));
    }

    private static List<String> getJsonObjects(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.trim().isEmpty()) return result;

        int depth = 0;
        int start = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '{' || c == '[') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}' || c == ']') {
                depth--;
                if (depth == 0) {
                    String obj = s.substring(start, i + 1).trim();
                    if (!obj.isEmpty()) {
                        result.add(obj);
                    }
                }
            }
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public String getLevelString() {
        return levelString;
    }

    public LevelSettings getSettings() {
        return settings;
    }

    public List<PlayerSpawn> getPlayerSpawns() {
        return new ArrayList<>(playerSpawns);
    }

    public List<TankSpawn> getTankSpawns() {
        return new ArrayList<>(tankSpawns);
    }

    public List<ObstacleSpawn> getObstacleSpawns() {
        return new ArrayList<>(obstacleSpawns);
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    public Team getTeam(String name) {
        return teamsMap.get(name);
    }

    public int getStartingCoins() {
        return startingCoins;
    }

    public List<Item.ShopItem> getShop() {
        return new ArrayList<>(shop);
    }

    public List<Item.ItemStack<?>> getStartingItems() {
        return new ArrayList<>(startingItems);
    }

    public List<ShopTankBuild> getPlayerBuilds() {
        return new ArrayList<>(playerBuilds);
    }

    public boolean isSynchronizeMusic() {
        return synchronizeMusic;
    }

    public int getBeatBlocks() {
        return beatBlocks;
    }

    public boolean isDisableFriendlyFire() {
        return disableFriendlyFire;
    }

    // Utility methods
    public boolean isEditable() {
        return settings.editable;
    }

    public boolean isTimed() {
        return settings.timed;
    }

    public double getTimer() {
        return settings.timer;
    }

    public int getSizeX() {
        return settings.sizeX;
    }

    public int getSizeY() {
        return settings.sizeY;
    }

    public Color getBackgroundColor() {
        return settings.backgroundColor.clone();
    }

    public boolean isDark() {
        Color bg = settings.backgroundColor;
        return bg.red * 0.2126 + bg.green * 0.7152 + bg.blue * 0.0722 <= 127 ||
                settings.lightIntensity <= 0.5;
    }

    public boolean isLarge() {
        return !(settings.sizeX * settings.sizeY <= 100000 && tankSpawns.size() < 500);
    }

    @Override
    public String toString() {
        return String.format("Level{size=%dx%d, players=%d, tanks=%d, obstacles=%d, teams=%d}",
                settings.sizeX, settings.sizeY,
                playerSpawns.size(), tankSpawns.size(),
                obstacleSpawns.size(), teams.size());
    }
}