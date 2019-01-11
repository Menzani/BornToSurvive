package it.menzani.bts.components.suicide;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
import it.menzani.bts.components.SimpleComponent;

public class Suicide extends SimpleComponent {
    public Suicide(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void load() {
        registerPlayerCommand("suicide");
    }

    @Override
    protected void onCommand(String command, User sender, String[] args) {
        sender.setHealth(0);
    }
}
