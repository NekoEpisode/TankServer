package net.neko.game.tank;

import net.neko.game.color.TankColor;
import net.neko.game.team.Team;

public class PlayerTank implements Tank {
    private final TankColor color;
    private double x;
    private double y;
    private Team team;

    public PlayerTank(TankColor color, double x, double y, Team team) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.team = team;
    }

    @Override
    public void update() {

    }

    @Override
    public TankColor getColor() {
        return color;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
