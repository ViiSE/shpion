package ru.viise.shpion;

import org.junit.jupiter.api.Test;
import ru.viise.shpion.fs.FsEvent;
import ru.viise.shpion.fs.SpyFiles;
import ru.viise.shpion.fs.SpyOptionsFs;
import ru.viise.shpion.fs.SpyWatcherFs;

import java.time.Duration;

public class SpyFilesTest {

    @Test
    public void watch() {
        Spy<Void> spyFiles = SpyFiles.create(
                new SpyOptionsFs(FsEvent.CREATE, FsEvent.DELETE, FsEvent.MODIFY)
                        .needPool(Duration.ofMillis(100L)),
                new SpyWatcherFs()
                        .from(
                                "anotherDirs\\Hello2.txt",
                                fsEventContext ->
                                        System.out.println("EVENT: " + fsEventContext.event() + ", AB_PATH: " + fsEventContext.absolutePath())
                        )
                        .from(
                                "exitDir\\exitnow",
                                fsEventContext -> {
                                    if (FsEvent.DELETE == fsEventContext.event()) {
                                        System.out.println("!!EXIT!!");
                                        fsEventContext.self().stop();
                                    }
                                })
        );

        spyFiles.watch();
    }
}
