package ru.viise.shpion.java;

import ru.viise.shpion.SpySelf;

public record JvMethodEventContext(
        Object target,
        Object[] args,
        Object result,
        Throwable methodErr,
        SpySelf self) {
}
