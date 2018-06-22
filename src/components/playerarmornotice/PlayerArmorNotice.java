package it.menzani.bts.components.playerarmornotice;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class PlayerArmorNotice extends SimpleComponent {
    private static final Set<Material> armorMaterials = EnumSet.of(
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);

    public PlayerArmorNotice(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;
        ItemStack item = event.getItem();
        Material type = item.getType();
        if (!armorMaterials.contains(type)) return;
        short maxDurability = type.getMaxDurability();
        float x = (float) item.getDurability() / maxDurability;
        if (Math.abs(x - 0.9F) > 0.5F / maxDurability) return;
        Player player = event.getPlayer();
        player.sendMessage("Your " + ChatColor.BLUE + prettifyMaterialName(type.name()) + ChatColor.RESET + " will break soon!");
    }

    private static String prettifyMaterialName(String name) {
        name = name.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
