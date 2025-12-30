package ru.viise.shpion;

import ru.viise.shpion.fs.FsKind;
import ru.viise.shpion.fs.FsOptions;
import ru.viise.shpion.fs.SpyDir;

import java.util.List;

public class SpyDirTest {

    static void main() throws InterruptedException {
        Thread dirThread = new Thread(() -> {
            FsOptions options = FsOptions.of(
                    "dirs",
                    List.of(FsKind.CREATE,
                            FsKind.MODIFY,
                            FsKind.DELETE),
                    List.of(
                            eventContext -> {
                                switch (eventContext.kind()) {
                                    case CREATE:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("CREATE: " + eventContext.path().toUri());
                                        break;
//                                    }
                                    case MODIFY:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("MODIFY: " + eventContext.path().toUri());
                                        break;
//                                    }
                                    case DELETE:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("DELETE: " + eventContext.path().toUri());
                                        eventContext.self().stop();
                                        break;
//                                    }
                                }
                            }
                    )
            );

            Spy spyDirDirs = new SpyDir(options);
            spyDirDirs.watch();
        });

        Thread downloadThread = new Thread(() -> {
            FsOptions downloadOptions = FsOptions.of(
                    "C:\\Users\\ViiSE\\Downloads",
                    List.of(FsKind.CREATE,
                            FsKind.MODIFY,
                            FsKind.DELETE),
                    List.of(
                            eventContext -> {
                                switch (eventContext.kind()) {
                                    case CREATE:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("CREATE: " + eventContext.path().toUri());
                                        break;
//                                    }
                                    case MODIFY:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("MODIFY: " + eventContext.path().toUri());
                                        break;
//                                    }
                                    case DELETE:
//                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("DELETE: " + eventContext.path().toUri());
                                        eventContext.self().stop();
                                        break;
//                                    }
                                }
                            }
                    )
            );

            Spy spyDirDownloads = new SpyDir(downloadOptions);
            spyDirDownloads.watch();
        });

        dirThread.start();
        downloadThread.start();

        dirThread.join();
        downloadThread.join();
    }
}
