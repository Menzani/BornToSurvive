package it.menzani.bts;

import org.bukkit.event.Listener;

public abstract class ListenerComponent implements Component, Listener {
    private final BornToSurvive bornToSurvive;

    protected ListenerComponent(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    @Override
    public void load() {
        bornToSurvive.registerListener(this);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getTag() {
        return '[' + getName() + "] ";
    }
}
