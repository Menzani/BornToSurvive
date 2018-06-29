package it.menzani.bts.configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

class EnumParseProblem implements Problem {
    private final Class<? extends Enum<?>> enumClass;

    EnumParseProblem(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String description() {
        return "must be one of: " + Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
    }
}
