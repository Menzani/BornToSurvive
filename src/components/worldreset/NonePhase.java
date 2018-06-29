package it.menzani.bts.components.worldreset;

import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

class NonePhase extends SimpleComponentListener {
    NonePhase(SimpleComponent component) {
        super(component);
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
