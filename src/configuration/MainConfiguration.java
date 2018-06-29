package it.menzani.bts.configuration;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.worldreset.Phase;
import it.menzani.bts.persistence.DatabaseCredentials;
import it.menzani.logger.api.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfiguration {
    private final FileConfiguration config;
    private final Logger logger;

    public MainConfiguration(BornToSurvive bornToSurvive) {
        bornToSurvive.saveDefaultConfig();

        config = bornToSurvive.getConfig();
        config.setDefaults(new MemoryConfiguration()); // Do not use embedded config.yml as a default.
        logger = bornToSurvive.getRootLogger();
    }

    private DatabaseCredentials databaseCredentials;
    private WorldReset worldReset;

    public DatabaseCredentials getDatabaseCredentials() {
        return databaseCredentials;
    }

    public WorldReset getWorldReset() {
        return worldReset;
    }

    public boolean validate() {
        Validation validation = new Validation();
        databaseCredentials = validateDatabase(validation);
        worldReset = validateWorldReset(validation);

        if (validation.isSuccessful()) return false;
        logger.fatal("config.yml - " + validation);
        return true;
    }

    private DatabaseCredentials validateDatabase(Validation validation) {
        ConfigurationSection database = config.getConfigurationSection("database");
        if (database == null) {
            validation.addProblem("database", SimpleProblem.NULL_SECTION);
            return null;
        }
        String host = database.getString("host");
        String name = database.getString("name");
        String user = database.getString("user");
        String password = database.getString("password");
        if (host == null || host.isEmpty()) {
            validation.addProblem(database, "host", SimpleProblem.NULL_OR_EMPTY);
        }
        if (name == null || name.isEmpty()) {
            validation.addProblem(database, "name", SimpleProblem.NULL_OR_EMPTY);
        }
        if (user == null || user.isEmpty()) {
            validation.addProblem(database, "user", SimpleProblem.NULL_OR_EMPTY);
        }
        if (password == null || password.isEmpty()) {
            validation.addProblem(database, "password", SimpleProblem.NULL_OR_EMPTY);
        }
        return new DatabaseCredentials(host, name, user, password);
    }

    private WorldReset validateWorldReset(Validation validation) {
        ConfigurationSection worldReset = config.getConfigurationSection("worldReset");
        if (worldReset == null) {
            validation.addProblem("worldReset", SimpleProblem.NULL_SECTION);
            return null;
        }
        String phaseName = worldReset.getString("phase");
        Phase phase = null;
        if (phaseName == null || phaseName.isEmpty()) {
            validation.addProblem(worldReset, "phase", SimpleProblem.NULL_OR_EMPTY);
        } else {
            try {
                phase = Phase.valueOf(phaseName.toUpperCase());
            } catch (IllegalArgumentException e) {
                validation.addProblem(worldReset, "phase", new EnumParseProblem(Phase.class));
            }
        }
        return new WorldReset(phase);
    }

    public static class WorldReset {
        private final Phase phase;

        private WorldReset(Phase phase) {
            this.phase = phase;
        }

        public Phase getPhase() {
            return phase;
        }
    }
}
