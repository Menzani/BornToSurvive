package it.menzani.bts.components;

import it.menzani.logger.api.Logger;

public interface Component {
    void load();

    void unload();

    String getName();

    Logger getLogger();
}
