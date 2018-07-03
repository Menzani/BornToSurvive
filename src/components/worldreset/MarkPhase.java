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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.sql.PreparedStatement;
import java.util.EnumSet;
import java.util.List;
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
        checkOne(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (!isSign(block)) return;
        if (getAttachedBlock(block).getType().isSolid()) return;

        removeChunk(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        checkAll(event.blockList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        checkAll(event.blockList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        checkOne(event.getBlock());
    }

    private void checkAll(List<Block> blocks) {
        blocks.stream()
                .filter(MarkPhase::isSign)
                .filter(block -> !blocks.contains(getAttachedBlock(block)))
                .forEach(this::removeChunk);
    }

    private void checkOne(Block block) {
        if (!isSign(block)) return;
        removeChunk(block);
    }

    private static Block getAttachedBlock(Block sign) {
        org.bukkit.material.Sign signState = (org.bukkit.material.Sign) sign.getState().getData();
        return sign.getRelative(signState.getAttachedFace());
    }

    private static boolean isSign(Block block) {
        if (signMaterials.contains(block.getType())) {
            return WorldReset.isMark(block.getState());
        }
        return false;
    }

    private void removeChunk(Block block) {
        getBornToSurvive().getDatabase().submit(new RemoveChunk(removeChunkStatement, new ChunkLocation(block.getChunk())), getComponent());
    }
}
