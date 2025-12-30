package ru.viise.shpion.fs;

import java.nio.file.WatchEvent;
import java.util.List;
import java.util.function.Consumer;

public record FsOptions(
            String path,
            List<FsKind> kinds,
            List<Consumer<FsEventContext>> handlers) {

        public static FsOptions of(String filePath, List<FsKind> kinds, List<Consumer<FsEventContext>> handlers) {
            return new FsOptions(filePath, kinds, handlers);
        }

        WatchEvent.Kind<?>[] toWatchEventKind() {
            return (WatchEvent.Kind<?>[]) kinds.stream()
                    .map(FsKind::toWatchEventKind)
                    .toList()
                    .toArray(new WatchEvent.Kind[0]);
        }
    }