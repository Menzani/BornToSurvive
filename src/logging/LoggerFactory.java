package it.menzani.bts.logging;

import it.menzani.logger.Pipeline;
import it.menzani.logger.api.Level;
import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerFactory {
    private final Path logFile;
    private final java.util.logging.Logger pluginLogger;
    private Level level;

    public LoggerFactory(Path logFile, java.util.logging.Logger pluginLogger) {
        this.logFile = logFile;
        this.pluginLogger = pluginLogger;
    }

    public boolean createLogFolder() {
        Path logFolder = logFile.getParent();
        try {
            Files.createDirectories(logFolder);
            return false;
        } catch (IOException e) {
            pluginLogger.severe("Could not create log folder.");
            e.printStackTrace();
            return true;
        }
    }

    public LoggerFactory withLevel(Level level) {
        this.level = level;
        return this;
    }

    public Logger createLogger() {
        if (level == null) {
            throw new IllegalStateException("level has not been initialized.");
        }
        return new LoggerGroup()
                .addLogger(new SynchronousLogger("bukkit")
                        .addPipeline(new Pipeline("")
                                .setVerbosity(level)
                                .addConsumer(new JavaLoggerConsumer(pluginLogger))
                                .addConsumer(new HigherVerbosityLevelsConsumer(pluginLogger))))
                .addLogger(new AsynchronousLogger("file")
                        .addPipeline(new Pipeline("")
                                .setVerbosity(level)
                                .setFormatter(new TimestampFormatter())
                                .addConsumer(new FileConsumer(logFile)))
                        .setDefaultParallelism());
    }
}
