package net.neko.game.tank;

import net.neko.game.color.TankColor;
import net.neko.game.team.Team;

public interface Tank {
    void update();
    TankColor getColor();
    double getX();
    double getY();
    Team getTeam();
}
