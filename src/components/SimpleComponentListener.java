package it.menzani.bts.components;

import it.menzani.bts.BornToSurvive;
import it.menzani.logger.api.Logger;
import org.bukkit.event.HandlerList;

public abstract class SimpleComponentListener implements ComponentListener {
    private final SimpleComponent component;
    private final BornToSurvive bornToSurvive;
    private final Logger logger;

    protected SimpleComponentListener(SimpleComponent component) {
        this.component = component;
        bornToSurvive = component.getBornToSurvive();
        logger = component.getLogger();
    }

    SimpleComponentListener() {
        component = null;
        bornToSurvive = null;
        logger = null;
    }

    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void register() {
        bornToSurvive.registerListener(this);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public SimpleComponent getComponent() {
        return component;
    }
}
