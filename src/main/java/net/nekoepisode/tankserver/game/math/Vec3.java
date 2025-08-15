package net.nekoepisode.tankserver.game.math;

class Vec3 {
    public double x, y, z;

    public Vec3() { this(0, 0, 0); }
    public Vec3(double x, double y) { this(x, y, 0); }
    public Vec3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Vec3 copy() { return new Vec3(x, y, z); }
    public Vec3 add(Vec3 v) { return new Vec3(x + v.x, y + v.y, z + v.z); }
    public Vec3 subtract(Vec3 v) { return new Vec3(x - v.x, y - v.y, z - v.z); }
    public Vec3 multiply(double s) { return new Vec3(x * s, y * s, z * s); }
    public double length() { return Math.sqrt(x * x + y * y + z * z); }
    public double length2D() { return Math.sqrt(x * x + y * y); }
    public Vec3 normalize() {
        double len = length();
        return len > 0 ? multiply(1.0 / len) : new Vec3();
    }
}