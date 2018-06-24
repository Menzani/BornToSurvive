package it.menzani.bts.components.floatingitem;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

public class FloatingItem extends SimpleComponent {
    private static final String name = "Money";

    public FloatingItem(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void load() {
        super.load();

        ComponentTask task = new PreventItemDeath(this);
        task.runTaskTimer(Duration.ofMinutes(5).minusSeconds(5));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        Item item = event.getItemDrop();
        if (!shouldFloat(item)) return;
        item.setGravity(false);
        item.setCustomName(ChatColor.AQUA + name);
        item.setCustomNameVisible(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        Chunk chunk = event.getChunk();
        processEntities(Arrays.stream(chunk.getEntities()));
    }

    static void processEntities(Stream<Entity> entities) {
        entities.filter(entity -> entity.getType() == EntityType.DROPPED_ITEM)
                .map(entity -> (Item) entity)
                .filter(FloatingItem::shouldFloat)
                .forEach(item -> {
                    item.remove();

                    Item clone = item.getWorld().dropItem(item.getLocation(), item.getItemStack());
                    clone.setVelocity(item.getVelocity());
                    clone.setPickupDelay(item.getPickupDelay());
                    clone.setGravity(item.hasGravity());
                    clone.setCustomName(item.getCustomName());
                    clone.setCustomNameVisible(item.isCustomNameVisible());
                });
    }

    private static boolean shouldFloat(Item item) {
        ItemStack stack = item.getItemStack();
        if (stack.getType() != Material.INK_SACK) return false;
        Dye dye = (Dye) stack.getData();
        if (dye.getColor() != DyeColor.BLUE) return false;
        ItemMeta meta = stack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(name);
    }
}
