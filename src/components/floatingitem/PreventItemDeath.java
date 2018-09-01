package it.menzani.bts.components.floatingitem;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.World;

import java.util.Collection;

class PreventItemDeath extends SimpleComponentTask {
    PreventItemDeath(SimpleComponent component) {
        super(component);
    }

    @Override
    public void run() {
        FloatingItem.processEntities(getBornToSurvive().getWorlds().stream()
                .map(World::getEntities)
                .flatMap(Collection::stream));
    }
}
