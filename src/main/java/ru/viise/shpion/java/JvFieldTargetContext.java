package ru.viise.shpion.java;

public record JvFieldTargetContext(
        Object newValue,
        Object oldValue) {
    public static JvFieldTargetContext of(Object newValue, Object oldValue) {
        return new JvFieldTargetContext(newValue, oldValue);
    }
}
