package net.neko.game.color;

public class Color {
    public double red, green, blue, alpha;

    public Color(double red, double green, double blue) {
        this(red, green, blue, 255);
    }

    public Color(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void set(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color clone() {
        return new Color(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return String.format("Color(%.0f, %.0f, %.0f, %.0f)", red, green, blue, alpha);
    }
}