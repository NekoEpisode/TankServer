package net.nekoepisode.tankserver.game.level;

import net.nekoepisode.tankserver.game.team.Team;

public class PlayerSpawn {
    public double x, y, angle;
    public Team team;

    public PlayerSpawn(double x, double y, double angle, Team team) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.team = team;
    }

    @Override
    public String toString() {
        return String.format("PlayerSpawn{x=%.1f, y=%.1f, angle=%.2f, team=%s}", x, y, angle, team);
    }
}