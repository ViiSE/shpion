package ru.viise.shpion.fs;

import ru.viise.shpion.Spy;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpyDir implements Spy {

    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final FsOptions options;

    public SpyDir(FsOptions options) {
        this.options = options;
    }

    @Override
    public void watch() {
        Path path = Paths.get(options.path());

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            WatchEvent.Kind<?>[] watchEventKind = options.toWatchEventKind();
            path.register(watchService, watchEventKind);

            while (!stopped.get()) {
                WatchKey key = watchService.poll(100, TimeUnit.MILLISECONDS);
                if (key == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path pathContext = (Path) event.context();

                    options.handlers().forEach(handler ->
                            handler.accept(new FsEventContext(
                                    pathContext,
                                    FsKind.toKind(kind),
                                    this
                            )));
                }

                boolean valid = key.reset();
                if (!valid) {
                    stopped.set(true);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }
}
