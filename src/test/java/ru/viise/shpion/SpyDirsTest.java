package ru.viise.shpion;

import org.junit.jupiter.api.Test;
import ru.viise.shpion.fs.FsEvent;
import ru.viise.shpion.fs.SpyDirs;
import ru.viise.shpion.fs.SpyOptionsFs;
import ru.viise.shpion.fs.SpyWatcherFs;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class SpyDirsTest {

    @Test
    public void spyDirsTest() {
        AtomicInteger dirCounter = new AtomicInteger(0);
        AtomicInteger anotherDirCounter = new AtomicInteger(0);

        Spy<Void> spyDirs = SpyDirs.create(
                new SpyOptionsFs(FsEvent.CREATE, FsEvent.DELETE, FsEvent.MODIFY)
                        .needCreateIfNotExists()
                        .needPool(Duration.ofMillis(100L)),
                new SpyWatcherFs()
                        .from(
                                "dirs",
                                fsEventContext ->
                                        System.out.println("dirs: EVENT=" + fsEventContext.event().name() + ", FILENAME=" + fsEventContext.absolutePath().toUri()),
                                _ -> dirCounter.incrementAndGet()
                        ).from(
                                "anotherDirs",
                                fsEventContext ->
                                        System.out.println("anotherDirs: EVENT=" + fsEventContext.event().name() + ", FILENAME=" + fsEventContext.absolutePath().toUri()),
                                _ -> anotherDirCounter.incrementAndGet()
                        ).from(
                                "exitDir",
                                fsEventContext ->
                                        System.out.println("exitDir: STOPPED, because FILE=" + fsEventContext.absolutePath().toUri() + " and EVENT=" + fsEventContext.event().name()),
                                fsEventContext -> fsEventContext.self().stop()
                        )
        );

        spyDirs.watch();
        System.out.println("dirCounter: " + dirCounter.get());
        System.out.println("anotherDirCounter: " + anotherDirCounter.get());
    }
}
