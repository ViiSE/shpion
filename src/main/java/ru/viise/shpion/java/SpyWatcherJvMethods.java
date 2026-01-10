package ru.viise.shpion.java;

import ru.viise.shpion.SpyTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SpyWatcherJvMethods<T_OBJ> {

    private final T_OBJ watchObject;
    private final List<SpyTarget<String, JvMethodEventContext>> targets = new ArrayList<>();

    public SpyWatcherJvMethods(T_OBJ watchObject) {
        this.watchObject = watchObject;
    }

    public SpyWatcherJvMethods<T_OBJ> from(
            String method,
            Consumer<JvMethodEventContext> handler) {
        targets.add(SpyTarget.of(method, handler));
        return this;
    }

    @SafeVarargs
    public final SpyWatcherJvMethods<T_OBJ> from(
            String method,
            Consumer<JvMethodEventContext>... handlers) {
        targets.add(SpyTarget.of(method, handlers));
        return this;
    }

    public SpyWatcherJvMethods<T_OBJ> from(
            String method,
            List<Consumer<JvMethodEventContext>> handlers) {
        targets.add(SpyTarget.of(method, handlers));
        return this;
    }

    Object watchObject() {
        return watchObject;
    }

    List<SpyTarget<String, JvMethodEventContext>> targets() {
        return targets;
    }
}
