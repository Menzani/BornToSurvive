package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.PreparedStatement;

public class WorldReset extends SimpleComponent {
    private static final String signText = "Do not reset me!";

    private final PreparedStatement addChunkStatement, removeChunkStatement;

    public WorldReset(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
        WrappedSQLDatabase database = bornToSurvive.getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) {
            addChunkStatement = removeChunkStatement = null;
            return;
        }

        addChunkStatement = preparedStatements[0];
        removeChunkStatement = preparedStatements[1];

        database.execute(new CreateTable(), this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getLine(0).equals(signText)) return;
        Block block = event.getBlock();

        Integer updateCount = (Integer) getBornToSurvive().getDatabase().submit(new AddChunk(addChunkStatement,
                new ChunkLocation(block.getChunk())), this);
        if (updateCount == null) return;

        if (updateCount.equals(0)) {
            block.breakNaturally();
            User player = new User(event.getPlayer());
            player.sendMessageFormat("That chunk is already marked.");
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
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
            return true;
        }
        Sign sign = (Sign) block.getState();
        return !sign.getLine(0).equals(signText);
    }

    private void removeChunk(Block block) {
        getBornToSurvive().getDatabase().submit(new RemoveChunk(removeChunkStatement, new ChunkLocation(block.getChunk())), this);
    }
}
