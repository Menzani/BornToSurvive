package it.menzani.bts.components;

import it.menzani.logger.api.Logger;

public interface Component {
    void load();

    String getName();

    Logger getLogger();
}
