package net.nekoepisode.tankserver.game.tank;

import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.team.Team;

public class PlayerTank extends BaseTank {
    private double mX, mY;

    public PlayerTank(TankColor color, double x, double y, double angle, double pitch, Team team) {
        super(color, x, y, angle, pitch, team);
        this.mX = x;
        this.mY = y;
    }

    public double getMX() { return mX; }
    public double getMY() { return mY; }
    public void setMX(double mX) { this.mX = mX; }
    public void setMY(double mY) { this.mY = mY; }

    @Override
    public void update() {
        super.update();
    }
}
