package it.menzani.bts.components.disablecommand;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Set;

public class DisableCommand extends SimpleComponent {
    private static final Set<String> consoleOnlyCommandLabels = Set.of(
            "plugins", "pl",
            "me",
            "seed",
            "version", "ver", "about", "icanhasbukkit",
            "help", "?"
    );
    private static final Set<String> disabledCommandLabels = Set.of(
            "reload", "rl"
    );
    private static final String cancelSuffix = "------";

    public DisableCommand(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String label = event.getMessage().substring(1);
        int firstSpace = label.indexOf(' ');
        if (firstSpace != -1) {
            label = label.substring(0, firstSpace);
        }
        if (label.startsWith("minecraft:")) {
            label = label.substring(10);
        } else if (label.startsWith("bukkit:")) {
            label = label.substring(7);
        }
        if (isDisabled(label) || consoleOnlyCommandLabels.contains(label)) {
            event.setMessage(label + cancelSuffix);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String label = event.getCommand();
        if (isDisabled(label)) {
            event.setCommand(label + cancelSuffix);
        }
    }

    private static boolean isDisabled(String label) {
        return disabledCommandLabels.contains(label);
    }
}
