package ru.viise.shpion.utils;

import ru.viise.shpion.SpyTarget;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SpyTargetUtils {

    public static <TRG, CTX> List<Consumer<CTX>> getHandlersByTarget(
            TRG target,
            List<SpyTarget<TRG, CTX>> targets) {
        Optional<SpyTarget<TRG, CTX>> spyTargetOpt = targets.stream()
                .filter(spyTarget -> spyTarget.target().equals(target))
                .findFirst();
        return spyTargetOpt.orElse(new SpyTarget<>(null, List.of())).handlers();
    }
}
