package ru.viise.shpion.java;

public final class SpyWatcherJv<T_OBJ> {

    public SpyWatcherJv() {}

    public SpyWatcherJvFields fields(Object watchObject) {
        return new SpyWatcherJvFields(watchObject);
    }

    public SpyWatcherJvMethods<T_OBJ> methods(T_OBJ watchObject) {
        return new SpyWatcherJvMethods<>(watchObject);
    }
}
