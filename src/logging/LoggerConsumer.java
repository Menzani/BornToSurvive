package it.menzani.bts.logging;

import it.menzani.logger.api.Consumer;
import it.menzani.logger.api.Level;
import it.menzani.logger.impl.StandardLevel;

import java.util.logging.Logger;

class LoggerConsumer implements Consumer {
    private final Logger logger;

    LoggerConsumer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void consume(String entry, Level level) {
        if (level instanceof StandardLevel) {
            consume(entry, (StandardLevel) level);
        } else if (level.isError()) {
            logger.severe(entry);
        } else {
            logger.info(entry);
        }
    }

    private void consume(String entry, StandardLevel level) {
        switch (level) {
            case TRACE:
                logger.finest(entry);
                break;
            case DEBUG:
                logger.finer(entry);
                break;
            case FINE:
                logger.fine(entry);
                break;
            case INFORMATION:
            case HEADER:
                logger.info(entry);
                break;
            case WARNING:
                logger.warning(entry);
                break;
            case FAILURE:
            case FATAL:
                logger.severe(entry);
                break;
        }
    }
}
