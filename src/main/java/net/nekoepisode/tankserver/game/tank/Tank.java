package net.nekoepisode.tankserver.game.tank;

import net.nekoepisode.tankserver.game.color.TankColor;
import net.nekoepisode.tankserver.game.team.Team;

public interface Tank {
    void update();
    TankColor getColor();
    double getX();
    double getY();
    Team getTeam();
}
