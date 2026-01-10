package ru.viise.shpion.java;

import java.util.function.Consumer;

public record JvHandler(
        JvKind kind,
        Consumer<JvFieldEventContext> consumer) {
    public static JvHandler forField(Consumer<JvFieldEventContext> consumer) {
        return new JvHandler(JvKind.FIELD, consumer);
    }

    public static JvHandler forMethod(Consumer<JvFieldEventContext> consumer) {
        return new JvHandler(JvKind.METHOD, consumer);
    }
}
