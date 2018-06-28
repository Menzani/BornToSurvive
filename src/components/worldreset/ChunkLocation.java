package it.menzani.bts.components.worldreset;

import org.bukkit.Chunk;

class ChunkLocation {
    final int x, z;

    ChunkLocation(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    ChunkLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkLocation that = (ChunkLocation) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "ChunkLocation{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
