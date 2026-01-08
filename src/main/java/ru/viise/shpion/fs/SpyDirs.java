package ru.viise.shpion.fs;

import ru.viise.shpion.Spy;
import ru.viise.shpion.SpyTarget;
import ru.viise.shpion.utils.SpyTargetUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SpyDirs implements Spy {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicInteger deleteCounter = new AtomicInteger(0);

    private final SpyOptionsFs options;
    private final SpyWatcherFs watcher;

    SpyDirs(SpyOptionsFs options, SpyWatcherFs watcher) {
        this.options = options;
        this.watcher = watcher;
    }

    public static SpyDirs create(SpyOptionsFs options, SpyWatcherFs watcher) {
        return new SpyDirs(options, watcher);
    }

    @Override
    public void watch() {
        List<SpyTarget<Path, FsEventContext>> spyTargets = watcher.targets();
        preparation(spyTargets);

        Map<WatchKey, Path> keyToPathMap = new HashMap<>();

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            WatchEvent.Kind<?>[] watchEventKind = options.toWatchEventKind();
            for (SpyTarget<Path, FsEventContext> spyTarget : spyTargets) {
                WatchKey key = spyTarget.target().register(watchService, watchEventKind);
                keyToPathMap.put(key, spyTarget.target());
            }

            if (options.optionsGeneral().needPool()) {
                watchInLoop(watchService, keyToPathMap);
            } else {
                iterateWatch(watchService, keyToPathMap);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void preparation(List<SpyTarget<Path, FsEventContext>> spyTargets) {
        for (Path dir : spyTargets.stream().map(SpyTarget::target).toList()) {
            if (!dir.toFile().exists()) {
                if (options.createIfNotExists()) {
                    try {
                        Files.createDirectory(dir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new IllegalArgumentException(dir + " doesn't exist");
                }
            }
        }
    }

    private void watchInLoop(WatchService watchService, Map<WatchKey, Path> keyToPathMap) throws InterruptedException {
        while (!stopped.get()) {
            WatchKey key = watchService.poll(options.optionsGeneral().pollDur().toMillis(), TimeUnit.MILLISECONDS);
            if (key == null) {
                continue;
            }
            Path dir = keyToPathMap.get(key);
            if (dir == null) {
                continue;
            }

            handle(key, dir);

            boolean valid = key.reset();
            if (!valid) {
                deleteCounter.incrementAndGet();
                if (deleteCounter.get() == watcher.targets().size()) {
                    stopped.set(true);
                }
            }
        }
    }

    private void iterateWatch(WatchService watchService, Map<WatchKey, Path> keyToPathMap) throws InterruptedException {
        WatchKey key = watchService.take();
        if (key == null) {
            return;
        }
        Path dir = keyToPathMap.get(key);
        if (dir == null) {
            return;
        }
        handle(key, dir);
        stopped.set(true);
    }

    private void handle(WatchKey key, Path dir) {
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path relativePath = (Path) event.context();
            Path absolutePath = dir.resolve(relativePath);
            SpyTargetUtils.getHandlersByTarget(dir, watcher.targets()).forEach(handler ->
                    handler.accept(new FsEventContext(
                            relativePath,
                            absolutePath,
                            FsEvent.toFsEvent(kind),
                            this
                    ))
            );
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }
}
