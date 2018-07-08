package it.menzani.bts.logging;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;
import it.menzani.logger.impl.StandardLevel;

import java.util.logging.Logger;

class HigherVerbosityLevelsConsumer implements Consumer {
    private static final Level threshold = StandardLevel.INFORMATION;

    private final Logger pluginLogger;

    HigherVerbosityLevelsConsumer(Logger pluginLogger) {
        this.pluginLogger = pluginLogger;
    }

    @Override
    public void consume(LogEntry entry, String formattedEntry) {
        Level level = entry.getLevel();
        if (level.compareTo(threshold) != Level.Verbosity.GREATER) return;
        pluginLogger.info(level.getMarker() + " - " + formattedEntry);
    }
}
