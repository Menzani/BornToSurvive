package it.menzani.bts.persistence;

import it.menzani.bts.components.worldreset.Phase;

import java.io.*;
import java.util.Properties;

public class PropertyStore extends Properties {
    private static final Properties defaults = new Properties();

    static {
        defaults.setProperty("lastWorldResetPhase", Phase.NONE.name());
    }

    private final File folder, file;

    public PropertyStore(File folder) {
        super(defaults);
        this.folder = folder;
        file = new File(folder, "propertyStore.properties");
    }

    public boolean load() {
        folder.mkdirs();
        try {
            file.createNewFile();
            try (Reader reader = new FileReader(file)) {
                load(reader);
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Phase getLastWorldResetPhase() {
        String phaseName = getProperty("lastWorldResetPhase");
        return Phase.valueOf(phaseName);
    }

    public void setLastWorldResetPhase(Phase phase) {
        setProperty("lastWorldResetPhase", phase.name());
        save();
    }
}
