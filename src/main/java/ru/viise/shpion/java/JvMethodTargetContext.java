package ru.viise.shpion.java;

public record JvMethodTargetContext(
        Object[] args,
        Object result,
        Throwable methodErr) {
    public static JvMethodTargetContext of(Object[] args, Object result, Throwable methodErr) {
        return new JvMethodTargetContext(args, result, methodErr);
    }

    public static JvMethodTargetContext of(Object[] args, Object result) {
        return new JvMethodTargetContext(args, result, null);
    }
}
