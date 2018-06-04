package it.menzani.bts.logging;

import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.FileConsumer;
import it.menzani.logger.impl.SynchronousLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerBuilder {
    private final Path logFile;
    private final java.util.logging.Logger pluginLogger;

    public LoggerBuilder(Path logFile, java.util.logging.Logger pluginLogger) {
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

    public Logger build() {
        return new SynchronousLogger()
                .setConsumers(new LoggerConsumer(pluginLogger), new FileConsumer(logFile))
                .setFormatter(new LevelFormatter());
    }
}
