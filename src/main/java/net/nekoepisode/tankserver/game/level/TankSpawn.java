package net.nekoepisode.tankserver.game.level;

import net.nekoepisode.tankserver.game.team.Team;

public class TankSpawn {
    public double x, y, angle;
    public String type;
    public Team team;
    public String metadata;

    public TankSpawn(double x, double y, String type, double angle, Team team, String metadata) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.angle = angle;
        this.team = team;
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return String.format("TankSpawn{type='%s', x=%.1f, y=%.1f, angle=%.2f, team=%s}",
                type, x, y, angle, team);
    }
}