package it.menzani.bts.components.optimize;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;

import java.time.LocalTime;

class Restarter extends SimpleComponentTask {
    Restarter(SimpleComponent component) {
        super(component);
    }

    @Override
    public void run() {
        LocalTime now = LocalTime.now();
        if (now.getHour() != 7 || now.getMinute() != 0 || now.getSecond() < 30) {
            return;
        }

        getBornToSurvive().getServer().shutdown();
    }
}
