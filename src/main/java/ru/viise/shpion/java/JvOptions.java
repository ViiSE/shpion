package ru.viise.shpion.java;

import java.time.Duration;
import java.util.List;

public record JvOptions(
        Object target,
        List<JvSubTarget> subTargets,
        List<JvHandler> handlers,
        Duration pollDur) {

    public static JvOptions of(Object target, List<JvSubTarget> subTargets, List<JvHandler> handlers) {
        return new JvOptions(target, subTargets, handlers, Duration.ofMillis(100L));
    }

    public static JvOptions of(
            Object target,
            List<JvSubTarget> subTargets,
            List<JvHandler> handlers,
            Duration pollDur) {
        return new JvOptions(target, subTargets, handlers, pollDur);
    }

    @Override
    public Duration pollDur() {
        return pollDur == null ? Duration.ofMillis(100L) : pollDur;
    }
}
