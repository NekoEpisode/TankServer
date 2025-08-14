package net.nekoepisode.tankserver.game.level;

public class ObstacleSpawn {
    public double x, y;
    public String type;
    public String metadata;

    public ObstacleSpawn(double x, double y, String type, String metadata) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return String.format("ObstacleSpawn{type='%s', x=%.1f, y=%.1f}", type, x, y);
    }
}