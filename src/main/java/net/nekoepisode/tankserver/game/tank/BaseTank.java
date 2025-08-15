package net.nekoepisode.tankserver.game.tank;

import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.team.Team;

public class BaseTank implements Tank {
    private Team team;
    private TankColor color;

    private double x, y;
    private double vX, vY;

    private double angle;
    private double pitch;

    private boolean action1;
    private boolean action2;
    private boolean[] quickActions = new boolean[5];

    private double time;

    public BaseTank(TankColor color, double x, double y, double angle, double pitch, Team team) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.team = team;
        this.angle = angle;
        this.pitch = pitch;
    }

    @Override public Team getTeam() { return team; }
    @Override public void setTeam(Team team) { this.team = team; }
    @Override public TankColor getColor() { return color; }
    @Override public void setColor(TankColor color) { this.color = color; }

    @Override public double getX() { return x; }
    @Override public void setX(double x) { this.x = x; }
    @Override public double getY() { return y; }
    @Override public void setY(double y) { this.y = y; }
    @Override public double getVX() { return vX; }
    @Override public void setVX(double vX) { this.vX = vX; }
    @Override public double getVY() { return vY; }
    @Override public void setVY(double vY) { this.vY = vY; }

    @Override public double getAngle() { return angle; }
    @Override public void setAngle(double angle) { this.angle = angle; }
    @Override public double getPitch() { return pitch; }
    @Override public void setPitch(double pitch) { this.pitch = pitch; }

    @Override public boolean getAction1() { return action1; }
    @Override public void setAction1(boolean action1) { this.action1 = action1; }
    @Override public boolean getAction2() { return action2; }
    @Override public void setAction2(boolean action2) { this.action2 = action2; }
    @Override public boolean[] getQuickActions() { return quickActions; }
    @Override public void setQuickActions(boolean[] quickActions) { this.quickActions = quickActions; }

    @Override public double getTime() { return time; }
    @Override public void setTime(double time) { this.time = time; }

    @Override public void update() {
        // TODO: Complete update logic
    }
}
