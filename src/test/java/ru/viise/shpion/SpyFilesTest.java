package ru.viise.shpion;

import org.junit.jupiter.api.Test;
import ru.viise.shpion.fs.FsEvent;
import ru.viise.shpion.fs.SpyFiles;

import java.time.Duration;

public class SpyFilesTest {

    @Test
    public void watch() {
        Spy spyFiles = SpyFiles.create(
                SpyOptions.fs(FsEvent.CREATE, FsEvent.DELETE, FsEvent.MODIFY)
                        .needPool(Duration.ofMillis(100L)),
                SpyWatcher.fs()
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
