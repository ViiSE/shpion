package ru.viise.shpion.fs;

import ru.viise.shpion.Spy;
import ru.viise.shpion.SpyPoolable;
import ru.viise.shpion.SpyTarget;
import ru.viise.shpion.utils.Pair;
import ru.viise.shpion.utils.SpyTargetUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SpyFiles implements SpyPoolable {

    private final SpyOptionsFs options;
    private final SpyWatcherFs watcher;

    private final Spy<Void> spyDirs;

    SpyFiles(SpyOptionsFs options, SpyWatcherFs watcher) {
        this.options = options;
        this.watcher = watcher;
        this.spyDirs = createSpyDirs();
    }

    public static SpyFiles create(SpyOptionsFs options, SpyWatcherFs watcher) {
        return new SpyFiles(options, watcher);
    }

    private Spy<Void> createSpyDirs() {
        SpyWatcherFs spyWatcherFsDirs = new SpyWatcherFs();
        /*
        Алгоритм следующий:
        1. Делаем Map, где ключ - parentDir файлов, значение - все SpyTarget файлов, которые содержатся в папке parentDir
        2. Для каждой этой папки создаём watcher'а, который будет смотреть, по какому файлу пришло событие
        3. Для этого файла вызываем все его handler'ы
         */
        Map<String, Pair<Path, List<SpyTarget<Path, FsEventContext>>>> parentDirSpyTargetPairsMap = new HashMap<>();
        watcher.targets().forEach(spyTarget -> {
            Path parentDir = spyTarget.target().getParent();
            if (parentDirSpyTargetPairsMap.containsKey(parentDir.toString())) {
                parentDirSpyTargetPairsMap.get(parentDir.toString()).right().add(spyTarget);
            } else {
                List<SpyTarget<Path, FsEventContext>> spyTargets = new ArrayList<>();
                spyTargets.add(spyTarget);
                parentDirSpyTargetPairsMap.put(parentDir.toString(), new Pair<>(parentDir, spyTargets));
            }
        });

        parentDirSpyTargetPairsMap.forEach((_, spyPair) ->
                spyWatcherFsDirs.from(
                        spyPair.left(),
                        fsEventContext -> {
                            Path filePath = fsEventContext.absolutePath();
                            List<Consumer<FsEventContext>> fileHandlers = SpyTargetUtils.getHandlersByTarget(
                                    filePath,
                                    spyPair.right()
                            );
                            if (!fileHandlers.isEmpty()) {
                                fileHandlers.forEach(handler -> handler.accept(fsEventContext));
                            }
                        }
                ));

        return SpyDirs.create(
                options,
                spyWatcherFsDirs
        );
    }

    @Override
    public Void watch() {
        spyDirs.watch();
        return null;
    }

    @Override
    public void stop() {
        spyDirs.stop();
    }
}
