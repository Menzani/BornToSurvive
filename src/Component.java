package it.menzani.bts;

public interface Component {
    void load();

    default String getName() {
        return getClass().getName();
    }

    default String getTag() {
        return '[' + getName() + "] ";
    }
}
