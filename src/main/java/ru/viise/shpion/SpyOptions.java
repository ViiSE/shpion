package ru.viise.shpion;

import ru.viise.shpion.fs.FsEvent;
import ru.viise.shpion.fs.SpyOptionsFs;

import java.util.List;

public final class SpyOptions {

    SpyOptions() {}

    public static SpyOptionsFs fs(FsEvent event) {
        return SpyOptionsFs.of(event);
    }

    public static SpyOptionsFs fs(FsEvent... events) {
        return SpyOptionsFs.of(events);
    }

    public static SpyOptionsFs fs(List<FsEvent> events) {
        return SpyOptionsFs.of(events);
    }
}
