package it.menzani.bts.components.worldreset;

import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import it.menzani.logger.Profiler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import java.util.*;
import java.util.stream.Collectors;

class NonePhase extends SimpleComponentListener {
    final MarkedArea markedArea;

    NonePhase(SimpleComponent component, MarkedArea markedArea) {
        super(component);
        this.markedArea = markedArea;
    }

    @Override
    public void register() {
        super.register();

        Phase lastPhase = getBornToSurvive().getPropertyStore().getWorldReset().getLastPhase();
        if (lastPhase != Phase.MARK) return;
        getLogger().info("Removing marks placed during last mark phase");
        Set<Location> failures;
        try (Profiler ignored = getBornToSurvive().newProfiler("Removing marks")) {
            failures = markedArea.marks.values().stream()
                    .flatMap(Collection::stream)
                    .map(ChunkLocation::toChunk)
                    .map(Chunk::getTileEntities)
                    .flatMap(Arrays::stream)
                    .filter(MarkPhase::isSign)
                    .map(state -> {
                        state.setType(Material.AIR);
                        boolean successful = state.update(true);
                        if (successful) return null;
                        assert state.isPlaced();
                        return state.getLocation();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        if (!failures.isEmpty()) {
            Map<World, List<Location>> byWorld = failures.stream()
                    .collect(Collectors.groupingBy(Location::getWorld));
            StringBuilder builder = new StringBuilder("Could not remove these marks:");
            for (var entry : byWorld.entrySet()) {
                builder.append(System.lineSeparator());
                builder.append("  ");
                builder.append(entry.getKey().getName());
                builder.append(':');
                for (Location location : entry.getValue()) {
                    builder.append(System.lineSeparator());
                    builder.append("    ");
                    builder.append(location.getBlockX());
                    builder.append(' ');
                    builder.append(location.getBlockY());
                    builder.append(' ');
                    builder.append(location.getBlockZ());
                }
            }
            getLogger().fail(builder);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getLine(0).equals(WorldReset.signText)) return;
        Block block = event.getBlock();
        block.breakNaturally();
        User player = new User(event.getPlayer());
        player.sendMessageFormat("Marking chunks is not allowed currently.");
    }
}
