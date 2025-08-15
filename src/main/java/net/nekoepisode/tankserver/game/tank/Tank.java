package net.nekoepisode.tankserver.game.tank;

import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.team.Team;

public interface Tank {
    Team getTeam();
    void setTeam(Team team);
    TankColor getColor();
    void setColor(TankColor color);

    double getX();
    void setX(double x);
    double getY();
    void setY(double y);
    double getVX();
    void setVX(double vX);
    double getVY();
    void setVY(double vY);

    double getAngle();
    void setAngle(double angle);
    double getPitch();
    void setPitch(double pitch);

    boolean getAction1();
    void setAction1(boolean action1);
    boolean getAction2();
    void setAction2(boolean action2);
    boolean[] getQuickActions();
    void setQuickActions(boolean[] quickActions);

    double getTime();
    void setTime(double time);

    void update();
}