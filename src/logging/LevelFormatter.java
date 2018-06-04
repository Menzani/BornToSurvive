package it.menzani.bts.logging;

import it.menzani.logger.EvaluationException;
import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Formatter;

class LevelFormatter implements Formatter {
    @Override
    public String format(LogEntry entry) throws EvaluationException {
        return '[' + entry.getLevel().getMarker() + "] " + entry.getMessage();
    }
}
