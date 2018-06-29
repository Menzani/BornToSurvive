package it.menzani.bts.components.worldreset;

import org.bukkit.Chunk;

class ChunkLocation {
    private static final char compactStringSeparatorChar = ',';
    private static final String compactStringSeparator = Character.toString(compactStringSeparatorChar);

    static ChunkLocation fromCompactString(String string) throws ParseException {
        int indexOfSeparator = string.indexOf(compactStringSeparatorChar);
        if (indexOfSeparator == -1) {
            throw new ParseException("Separator not found.");
        }
        int x, z;
        try {
            x = Integer.parseInt(string.substring(0, indexOfSeparator));
            z = Integer.parseInt(string.substring(indexOfSeparator + 1));
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid coordinate.", e);
        }
        return new ChunkLocation(x, z);
    }

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

    String toCompactString() {
        return x + compactStringSeparator + z;
    }
}
