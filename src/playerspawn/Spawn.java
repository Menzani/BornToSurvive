package it.menzani.bts.playerspawn;

public class Spawn {
    public final int x, y, z;

    public Spawn(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Spawn{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
