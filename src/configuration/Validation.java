package it.menzani.bts.configuration;

import org.bukkit.configuration.ConfigurationSection;

class Validation {
    private final StringBuilder problems = new StringBuilder();
    private short problemCount;

    void addProblem(String field, Problem problem) {
        addProblem(null, field, problem);
    }

    void addProblem(ConfigurationSection section, String field, Problem problem) {
        problems.append(System.lineSeparator());
        if (section != null) {
            problems.append(section.getCurrentPath());
            problems.append('.');
        }
        problems.append(field);
        problems.append(' ');
        problems.append(problem.description());
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
