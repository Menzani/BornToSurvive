package it.menzani.bts.logging;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;
import it.menzani.logger.impl.StandardLevel;

import java.util.logging.Logger;

class HigherVerbosityLevelsConsumer implements Consumer {
    private static final int threshold = StandardLevel.FINE.getVerbosity();

    private final Logger pluginLogger;

    HigherVerbosityLevelsConsumer(Logger pluginLogger) {
        this.pluginLogger = pluginLogger;
    }

    @Override
    public void consume(String entry, Level level) {
        if (level.getVerbosity() < threshold) return;
        pluginLogger.info(level.getMarker() + " - " + entry);
    }
}
