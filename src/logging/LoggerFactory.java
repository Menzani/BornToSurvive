package it.menzani.bts.logging;

import it.menzani.logger.Pipeline;
import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerFactory {
    private final Path logFile;
    private final java.util.logging.Logger pluginLogger;

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
            System.err.println("Could not create log folder.");
            e.printStackTrace();
            return true;
        }
    }

    public Logger createLogger() {
        LoggerSet loggerSet = new LoggerSet();
        loggerSet.add(new SynchronousLogger().setPipelines(
                new Pipeline().addConsumer(new JavaLoggerConsumer(pluginLogger))));
        loggerSet.add(new AsynchronousLogger().setPipelines(
                Pipeline.newConsoleLocalPipeline().setConsumers(new FileConsumer(logFile))));
        return loggerSet;
    }
}
