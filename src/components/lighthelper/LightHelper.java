package it.menzani.bts.components.lighthelper;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class LightHelper extends SimpleComponent {
    private final Map<Player, ComponentTask> tasks = new HashMap<>();

    public LightHelper(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void load() {
        registerPlayerCommand("light");
    }

    @Override
    protected void onCommand(String command, User sender, String[] args) {
        if (cancelIfActive(sender)) {
            ComponentTask task = new DarknessFinder(this, sender);
            task.runTaskTimer();
            tasks.put(sender, task);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cancelIfActive(player);
    }

    private boolean cancelIfActive(Player player) {
        ComponentTask task = tasks.remove(player);
        if (task == null) {
            return true;
        }
        task.cancel();
        return false;
    }
}
