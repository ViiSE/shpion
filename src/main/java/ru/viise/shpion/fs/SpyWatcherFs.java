package ru.viise.shpion.fs;

import ru.viise.shpion.SpyTarget;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SpyWatcherFs {

    private final List<SpyTarget<Path, FsEventContext>> targets = new ArrayList<>();

    SpyWatcherFs() {
    }

    public static SpyWatcherFs create() {
        return new SpyWatcherFs();
    }

    public SpyWatcherFs from(
            String path,
            Consumer<FsEventContext> handler) {
        targets.add(SpyTarget.of(Paths.get(path), handler));
        return this;
    }

    @SafeVarargs
    public final SpyWatcherFs from(
            String path,
            Consumer<FsEventContext>... handlers) {
        targets.add(SpyTarget.of(Paths.get(path), handlers));
        return this;
    }

    public SpyWatcherFs from(
            String path,
            List<Consumer<FsEventContext>> handlers) {
        targets.add(SpyTarget.of(Paths.get(path), handlers));
        return this;
    }

    public SpyWatcherFs from(
            Path path,
            Consumer<FsEventContext> handler) {
        targets.add(SpyTarget.of(path, handler));
        return this;
    }

    @SafeVarargs
    public final SpyWatcherFs from(
            Path path,
            Consumer<FsEventContext>... handlers) {
        targets.add(SpyTarget.of(path, handlers));
        return this;
    }

    public SpyWatcherFs from(
            Path path,
            List<Consumer<FsEventContext>> handlers) {
        targets.add(SpyTarget.of(path, handlers));
        return this;
    }

    List<SpyTarget<Path, FsEventContext>> targets() {
        return targets;
    }
}
