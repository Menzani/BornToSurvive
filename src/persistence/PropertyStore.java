package it.menzani.bts.persistence;

import it.menzani.bts.components.worldreset.Phase;

import java.io.*;
import java.util.Properties;

public class PropertyStore extends Properties {
    private static final Properties defaults = new Properties();

    private static final String worldResetProperty = "worldReset";

    static {
        defaults.setProperty(WorldReset.lastPhaseProperty, Phase.NONE.name());
    }

    private final File folder, file;
    private final Autosave autosave = new Autosave();

    private WorldReset worldReset;

    public PropertyStore(File folder) {
        super(defaults);
        this.folder = folder;
        file = new File(folder, "propertyStore.properties");
    }

    public WorldReset getWorldReset() {
        return worldReset;
    }

    public boolean load() {
        folder.mkdirs();
        try {
            file.createNewFile();
            try (Reader reader = new FileReader(file)) {
                load(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        loadWorldReset();

        autosave.start();
        return false;
    }

    private void loadWorldReset() {
        Phase lastPhase = Phase.valueOf(getProperty(WorldReset.lastPhaseProperty));
        worldReset = new WorldReset(lastPhase);
    }

    public void save() {
        autosave.save();
    }

    private class Autosave extends Thread {
        private Autosave() {
            super(Autosave.class.getSimpleName() + " daemon");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    System.err.println(getName() + " thread was interrupted.");
                    break;
                }
                boolean error = save();
                if (error) break;
            }
        }

        private boolean save() {
            saveWorldReset();

            try (Writer writer = new FileWriter(file)) {
                store(writer, null);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }

        private void saveWorldReset() {
            setProperty(WorldReset.lastPhaseProperty, worldReset.lastPhase.name());
        }
    }

    public static class WorldReset {
        private static final String lastPhaseProperty = worldResetProperty + ".lastPhase";

        private Phase lastPhase;

        private WorldReset(Phase lastPhase) {
            this.lastPhase = lastPhase;
        }

        public Phase getLastPhase() {
            return lastPhase;
        }

        public void setLastPhase(Phase lastPhase) {
            this.lastPhase = lastPhase;
        }
    }
}
