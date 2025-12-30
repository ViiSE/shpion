package ru.viise.shpion.java;

import ru.viise.shpion.SpySelf;

public record JvEventContext(
        Object target,
        JvSubTarget subTarget,
        JvFieldTargetContext fieldTargetContext,
        JvMethodTargetContext methodTargetContext,
        SpySelf self) {

    public static JvEventContext forField(
            Object target,
            JvSubTarget subTarget,
            JvFieldTargetContext fieldTargetContext,
            SpySelf self) {
        return new JvEventContext(target, subTarget, fieldTargetContext, null, self);
    }

    public static JvEventContext forMethod(
            Object target,
            JvSubTarget subTarget,
            JvMethodTargetContext methodTargetContext,
            SpySelf self) {
        return new JvEventContext(target, subTarget, null, methodTargetContext, self);
    }
}
