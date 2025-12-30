package ru.viise.shpion.fs;

import ru.viise.shpion.Spy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpyFile implements Spy {

    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final Path filePath;
    private final Spy spyDir;

    public SpyFile(FsOptions options) {
        this.filePath = createFilePath(options);
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException(options.path() + " doesn't exist");
        }
        spyDir = createSpyDir(options);
    }

    private Path createFilePath(FsOptions options) {
        Path filePath = Paths.get(options.path());
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException(options.path() + " doesn't exist");
        }
        return filePath;
    }

    SpyDir createSpyDir(FsOptions options) {
        return new SpyDir(
                FsOptions.of(
                        filePath.getParent().toString(),
                        options.kinds(),
                        List.of(fsEventContext -> {
                            Path fsPath = fsEventContext.path();
                            if (filePath.getFileName().equals(fsPath.getFileName())) {
                                options.handlers().forEach(handler ->
                                        handler.accept(new FsEventContext(
                                                filePath,
                                                fsEventContext.kind(),
                                                this
                                        )));
                                if (fsEventContext.kind() == FsKind.DELETE) {
                                    stopped.set(true);
                                }
                                if (stopped.get()) {
                                    fsEventContext.self().stop();
                                }
                            }
                        })
                )
        );
    }

    @Override
    public void watch() {
        spyDir.watch();
    }

    @Override
    public void stop() {
        stopped.set(true);
    }
}
