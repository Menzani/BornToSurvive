package it.menzani.bts.components.itembreaknotice;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ItemBreakNotice extends SimpleComponent {
    public ItemBreakNotice(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.getDamage() == 0) return;
        ItemStack item = event.getItem();
        Material type = item.getType();
        Damageable meta = (Damageable) item.getItemMeta();
        if (type.getMaxDurability() - meta.getDamage() != 9) return;
        Player player = event.getPlayer();
        player.sendMessage("Your " + ChatColor.BLUE + prettifyMaterialName(type.name()) + ChatColor.RESET + " will break soon!");
    }

    private static String prettifyMaterialName(String name) {
        name = name.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
