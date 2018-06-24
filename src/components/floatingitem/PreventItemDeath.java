package it.menzani.bts.components.floatingitem;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;

class PreventItemDeath extends SimpleComponentTask {
    PreventItemDeath(SimpleComponent component) {
        super(component);
    }

    @Override
    public void run() {
        FloatingItem.processEntities(getBornToSurvive().getWorlds()
                .flatMap(world -> world.getEntities().stream()));
    }
}
