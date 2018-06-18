package it.menzani.bts.components.playerspawn;

class Spawn {
    final int x, y, z;

    Spawn(int x, int y, int z) {
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
