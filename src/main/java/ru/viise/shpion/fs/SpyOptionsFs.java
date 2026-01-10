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

    public SpyOptionsFs(FsEvent event) {
        this(List.of(event));
    }

    public SpyOptionsFs(FsEvent... events) {
        this(Arrays.asList(events));
    }

    public SpyOptionsFs(List<FsEvent> events) {
        this(events, new SpyOptionsGeneral());
    }

    public SpyOptionsFs needPool(Duration polDur) {
        optionsGeneral.needPool(polDur);
        return this;
    }

    public SpyOptionsFs needCreateIfNotExists() {
        createIfNotExists = true;
        return this;
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
