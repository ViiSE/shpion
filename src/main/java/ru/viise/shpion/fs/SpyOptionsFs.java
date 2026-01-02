package ru.viise.shpion.fs;

import ru.viise.shpion.SpyOptionsGeneral;

import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class SpyOptionsFs {

    private final List<FsEvent> events;
    private final SpyOptionsGeneral optionsGeneral;
    private boolean createIfNotExists = false;

    SpyOptionsFs(List<FsEvent> events, SpyOptionsGeneral optionsGeneral) {
        this.events = events;
        this.optionsGeneral = optionsGeneral;
    }

    public static SpyOptionsFs of(FsEvent event) {
        return of(List.of(event));
    }

    public static SpyOptionsFs of(FsEvent... events) {
        return of(Arrays.asList(events));
    }

    public static SpyOptionsFs of(List<FsEvent> events) {
        return new SpyOptionsFs(events, new SpyOptionsGeneral());
    }

    public SpyOptionsFs needPool(Duration polDur) {
        optionsGeneral.needPool(polDur);
        return this;
    }

    public SpyOptionsFs needCreateIfNotExists() {
        createIfNotExists = true;
        return this;
    }

    List<FsEvent> events() {
        return events;
    }

    SpyOptionsGeneral optionsGeneral() {
        return optionsGeneral;
    }

    boolean createIfNotExists() {
        return createIfNotExists;
    }

    WatchEvent.Kind<?>[] toWatchEventKind() {
        return (WatchEvent.Kind<?>[]) events.stream()
                .map(FsEvent::toWatchEventKind)
                .toList()
                .toArray(new WatchEvent.Kind[0]);
    }
}
