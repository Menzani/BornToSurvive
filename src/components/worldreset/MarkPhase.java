package it.menzani.bts.components.worldreset;

import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import it.menzani.bts.persistence.sql.wrapper.Value;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.PreparedStatement;
import java.util.EnumSet;
import java.util.Set;

class MarkPhase extends SimpleComponentListener {
    private static final Set<Material> signMaterials = EnumSet.of(Material.SIGN_POST, Material.WALL_SIGN);

    private final PreparedStatement addChunkStatement, removeChunkStatement;

    MarkPhase(SimpleComponent component, PreparedStatement addChunkStatement, PreparedStatement removeChunkStatement) {
        super(component);
        this.addChunkStatement = addChunkStatement;
        this.removeChunkStatement = removeChunkStatement;
    }

    @Override
    public void register() {
        super.register();

        Phase lastPhase = getBornToSurvive().getPropertyStore().getWorldReset().getLastPhase();
        if (lastPhase == Phase.MARK) return;
        getLogger().info("Resetting chunks marked and reset to prepare for new mark phase");
        getBornToSurvive().getDatabase().execute(new PurgeTables(), getComponent());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getLine(0).equals(WorldReset.signText)) return;
        Block block = event.getBlock();

        Value<Integer> updateCount = getBornToSurvive().getDatabase().submit(new AddChunk(addChunkStatement,
                new ChunkLocation(block.getChunk())), getComponent());
        if (updateCount == null) return;

        if (updateCount.get() == 0) {
            block.breakNaturally();
            User player = new User(event.getPlayer());
            player.sendMessageFormat("That chunk is already marked.");
        } else {
            World world = block.getWorld();
            world.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isNotSign(block)) return;

        removeChunk(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (isNotSign(block)) return;
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
        Block attachedBlock = block.getRelative(sign.getAttachedFace());
        if (attachedBlock.getType().isSolid()) return;

        removeChunk(block);
    }

    private static boolean isNotSign(Block block) {
        if (signMaterials.contains(block.getType())) {
            return !WorldReset.isMark(block.getState());
        }
        return true;
    }

    private void removeChunk(Block block) {
        getBornToSurvive().getDatabase().submit(new RemoveChunk(removeChunkStatement, new ChunkLocation(block.getChunk())), getComponent());
    }
}
