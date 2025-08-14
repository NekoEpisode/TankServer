package net.neko.game.team;

import net.neko.game.color.Color;

public class Team {
    public String name;
    public boolean friendlyFire;
    public Color teamColor;

    public Team(String name) {
        this(name, false);
    }

    public Team(String name, boolean friendlyFire) {
        this.name = name;
        this.friendlyFire = friendlyFire;
        this.teamColor = new Color(255, 255, 255);
    }

    public Team(String name, boolean friendlyFire, double r, double g, double b) {
        this.name = name;
        this.friendlyFire = friendlyFire;
        this.teamColor = new Color(r, g, b);
    }

    @Override
    public String toString() {
        return String.format("Team{name='%s', friendlyFire=%s}", name, friendlyFire);
    }
}