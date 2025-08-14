package net.neko.game.color;

public class TankColor {
    private final Color color1;
    private final Color color2;
    private final Color color3;

    public TankColor(Color color1, Color color2, Color color3) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    public Color getColor1() {
        return color1;
    }

    public Color getColor2() {
        return color2;
    }

    public Color getColor3() {
        return color3;
    }
}
