package it.menzani.bts.components;

import org.bukkit.event.Listener;

public interface ComponentListener extends ComponentElement, Listener {
    void register();
}
