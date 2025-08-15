package net.nekoepisode.tankserver.game.player;

import net.nekoepisode.tankserver.game.color.TankColor;

public class PlayerSettings {
    private TankColor tankColor;

    public PlayerSettings(TankColor tankColor) {
        this.tankColor = tankColor;
    }

    public TankColor getTankColor() {
        return tankColor;
    }

    public void setTankColor(TankColor tankColor) {
        this.tankColor = tankColor;
    }
}
