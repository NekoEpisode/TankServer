package net.neko.game.level;

import net.neko.game.color.Color;

public class LevelSettings {
    public int sizeX, sizeY;
    public Color backgroundColor;
    public Color backgroundVariation;
    public boolean editable = true;
    public boolean timed = false;
    public double timer = 0;
    public double lightIntensity = 1.0;
    public double shadowIntensity = 0.5;
    public int tilesRandomSeed;

    public LevelSettings() {
        this.backgroundColor = new Color(235, 207, 166);
        this.backgroundVariation = new Color(20, 20, 20);
        this.tilesRandomSeed = (int) (Math.random() * Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return String.format("LevelSettings{size=%dx%d, editable=%s, timed=%s}",
                sizeX, sizeY, editable, timed);
    }
}