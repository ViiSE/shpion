package ru.viise.shpion;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record SpyTarget<TRG, CTX>(
        TRG target,
        List<Consumer<CTX>> handlers) {

    public static <TRG, CTX> SpyTarget<TRG, CTX> of(TRG target, Consumer<CTX> handler) {
        return new SpyTarget<>(target, List.of(handler));
    }

    @SafeVarargs
    public static <TRG, CTX> SpyTarget<TRG, CTX> of(TRG target, Consumer<CTX>... handlers) {
        return new SpyTarget<>(target, Arrays.asList(handlers));
    }

    public static <TRG, CTX> SpyTarget<TRG, CTX> of(TRG target, List<Consumer<CTX>> handlers) {
        return new SpyTarget<>(target, handlers);
    }
}
