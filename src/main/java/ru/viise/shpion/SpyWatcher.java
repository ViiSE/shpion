package ru.viise.shpion;

import ru.viise.shpion.fs.SpyWatcherFs;

public final class SpyWatcher {

    SpyWatcher() {}

    public static SpyWatcherFs fs() {
        return SpyWatcherFs.create();
    }
}
