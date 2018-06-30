package it.menzani.bts.components.worldreset;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class MarkedArea {
    final Map<World, Set<ChunkLocation>> marks = new HashMap<>();
    final Map<World, Set<ChunkLocation>> area = new HashMap<>();
}
