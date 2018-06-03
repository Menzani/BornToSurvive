package it.menzani.bts.config;

import org.bukkit.configuration.ConfigurationSection;

class Validation {
    private final StringBuilder problems = new StringBuilder();
    private short problemCount;

    void addProblem(ConfigurationSection section, String field, Problem problem) {
        problems.append(System.lineSeparator());
        problems.append(section.getName());
        problems.append('.');
        problems.append(field);
        problems.append(' ');
        problems.append(problem);
        problems.append('.');

        problemCount++;
    }

    boolean isSuccessful() {
        return problemCount == 0;
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            throw new IllegalStateException("Validation is successful. " +
                    "Always check with #isSuccessful() before calling this method.");
        }
        return problemCount + " problems found:" + problems;
    }
}
