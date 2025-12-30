package ru.viise.shpion.fs;

import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

public record FsOptions(
        String path,
        List<FsKind> kinds,
        List<Consumer<FsEventContext>> handlers,
        Duration pollDur) {

    public static FsOptions of(String filePath, List<FsKind> kinds, List<Consumer<FsEventContext>> handlers) {
        return new FsOptions(filePath, kinds, handlers, Duration.ofMillis(100L));
    }

    public static FsOptions of(
            String filePath,
            List<FsKind> kinds,
            List<Consumer<FsEventContext>> handlers,
            Duration pollDur) {
        return new FsOptions(filePath, kinds, handlers, pollDur);
    }

    @Override
    public Duration pollDur() {
        return pollDur == null ? Duration.ofMillis(100L) : pollDur;
    }

    WatchEvent.Kind<?>[] toWatchEventKind() {
        return (WatchEvent.Kind<?>[]) kinds.stream()
                .map(FsKind::toWatchEventKind)
                .toList()
                .toArray(new WatchEvent.Kind[0]);
    }
}
