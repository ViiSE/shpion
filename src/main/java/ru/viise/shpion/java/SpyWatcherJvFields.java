package ru.viise.shpion.java;

import ru.viise.shpion.SpyTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SpyWatcherJvFields {

    private final Object watchObject;
    private final List<SpyTarget<String, JvFieldEventContext>> targets = new ArrayList<>();

    public SpyWatcherJvFields(Object watchObject) {
        this.watchObject = watchObject;
    }

    public SpyWatcherJvFields from(
            String field,
            Consumer<JvFieldEventContext> handler) {
        targets.add(SpyTarget.of(field, handler));
        return this;
    }

    @SafeVarargs
    public final SpyWatcherJvFields from(
            String field,
            Consumer<JvFieldEventContext>... handlers) {
        targets.add(SpyTarget.of(field, handlers));
        return this;
    }

    public SpyWatcherJvFields from(
            String field,
            List<Consumer<JvFieldEventContext>> handlers) {
        targets.add(SpyTarget.of(field, handlers));
        return this;
    }

    Object watchObject() {
        return watchObject;
    }

    List<SpyTarget<String, JvFieldEventContext>> targets() {
        return targets;
    }
}
