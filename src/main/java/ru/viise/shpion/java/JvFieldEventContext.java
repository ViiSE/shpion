package ru.viise.shpion.java;

import ru.viise.shpion.SpySelf;

import java.lang.reflect.Field;

public record JvFieldEventContext(
        Object watchObject,
        String fieldName,
        Field field,
        Object newValue,
        Object oldValue,
        SpySelf self) {
}
