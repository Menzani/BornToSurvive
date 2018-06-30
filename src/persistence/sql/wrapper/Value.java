package it.menzani.bts.persistence.sql.wrapper;

public class Value<T> {
    private final T value;

    Value(T value) {
        this.value = value;
    }

    public boolean isNull() {
        return value == null;
    }

    public T get() {
        if (isNull())
            throw new NullPointerException("Value is null. Always check with #isNull() before calling this method.");
        return value;
    }
}
