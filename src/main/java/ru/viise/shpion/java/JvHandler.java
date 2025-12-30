package ru.viise.shpion.java;

import java.util.function.Consumer;

public record JvHandler(
        JvKind kind,
        Consumer<JvEventContext> consumer) {
    public static JvHandler forField(Consumer<JvEventContext> consumer) {
        return new JvHandler(JvKind.FIELD, consumer);
    }

    public static JvHandler forMethod(Consumer<JvEventContext> consumer) {
        return new JvHandler(JvKind.METHOD, consumer);
    }
}
