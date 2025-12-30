package ru.viise.shpion.java;

public record JvSubTarget(
        String name,
        JvKind kind) {

    public static JvSubTarget method(String name) {
        return new JvSubTarget(name, JvKind.METHOD);
    }

    public static JvSubTarget field(String name) {
        return new JvSubTarget(name, JvKind.FIELD);
    }
}
