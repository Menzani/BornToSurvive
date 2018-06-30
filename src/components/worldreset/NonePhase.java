package it.menzani.bts.components.worldreset;

import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.Collection;

class NonePhase extends SimpleComponentListener {
    final MarkedArea markedArea;

    NonePhase(SimpleComponent component, MarkedArea markedArea) {
        super(component);
        this.markedArea = markedArea;
    }

    @Override
    public void register() {
        super.register();

        Phase lastWorldResetPhase = getBornToSurvive().getPropertyStore().getLastWorldResetPhase();
        if (lastWorldResetPhase != Phase.MARK) return;
        markedArea.marks.values().stream()
                .flatMap(Collection::stream)
                .map(ChunkLocation::toChunk)
                .map(Chunk::getTileEntities)
                .flatMap(Arrays::stream)
                .filter(WorldReset::isMark)
                .forEach(state -> {
                    state.setType(Material.AIR);
                    state.update(true);
                });
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
