package net.nekoepisode.tankserver.game.tank;

public class ShopTankBuild {
    public String name = "Default Build";
    public boolean enableTertiaryColor = false;

    public static ShopTankBuild fromString(String data) {
        ShopTankBuild build = new ShopTankBuild();
        // TODO: Complete parsing logic
        return build;
    }

    @Override
    public String toString() {
        return String.format("ShopTankBuild{name='%s'}", name);
    }
}