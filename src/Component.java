package it.menzani.bts;

public interface Component {
    void load();

    default String getName() {
        return getClass().getSimpleName();
    }

    default String getTag() {
        return '[' + getName() + "] ";
    }
}
