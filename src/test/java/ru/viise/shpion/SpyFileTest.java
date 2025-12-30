package ru.viise.shpion;

import ru.viise.shpion.fs.FsKind;
import ru.viise.shpion.fs.FsOptions;
import ru.viise.shpion.fs.SpyFile;

import java.util.List;

public class SpyFileTest {

    static void main() {
        FsOptions options = FsOptions.of(
                "dirs\\Test.txt",
                List.of(FsKind.CREATE,
                        FsKind.MODIFY,
                        FsKind.DELETE),
                List.of(
                        eventContext -> {
                            switch (eventContext.kind()) {
                                case CREATE:
                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("CREATE: " + eventContext.path().toUri());
                                        break;
                                    }
                                case MODIFY:
                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("MODIFY: " + eventContext.path().toUri());
                                        break;
                                    }
                                case DELETE:
                                    if (!eventContext.path().toUri().toString().endsWith("~")) {
                                        System.out.println("DELETE: " + eventContext.path().toUri());
                                        break;
                                    }
                            }
                        }
                )
        );

        Spy spyFile = new SpyFile(options);
        spyFile.watch();
    }
}
