package ru.viise.shpion.java;

import ru.viise.shpion.Spy;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpyJavaField implements Spy {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final boolean inPoll;

    private final Object target;
    private final Duration pollDur;
    private final List<JvHandler> handlers;

    private final List<JvSubTarget> fieldTargets;

    private final Map<String, Object> oldFieldValues = new HashMap<>();

    public SpyJavaField(JvOptions options, boolean inPoll) throws NoSuchFieldException, IllegalAccessException {
        this.inPoll = inPoll;

        this.target = options.target();
        this.pollDur = options.pollDur();

        this.handlers = options.handlers().stream()
                .filter(jvHandler -> jvHandler.kind() == JvKind.FIELD)
                .toList();

        this.fieldTargets = options.subTargets().stream()
                .filter(jvSubTarget -> jvSubTarget.kind() == JvKind.FIELD)
                .toList();

        initFields();
    }

    @Override
    public void watch() {
        if (inPoll) {
            watchInLoop();
        } else {
            iterateWatch();
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }

    @SuppressWarnings("BusyWait")
    private void watchInLoop() {
        try {
            while (!stopped.get()) {
                iterateWatch();
                Thread.sleep(pollDur.toMillis());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void iterateWatch() {
        try {
            for (JvSubTarget fieldTarget : fieldTargets) {
                checkFieldValue(fieldTarget);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFieldValue(JvSubTarget fieldTarget) throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        Field field = targetClass.getDeclaredField(fieldTarget.name());
        field.setAccessible(true);

        Object currentFieldValue = field.get(this.target);
        Object oldFieldValue = oldFieldValues.get(fieldTarget.name());

        if (needToHandle(currentFieldValue, oldFieldValue)) {
            handlers.forEach(handler -> handler.consumer().accept(
                    JvEventContext.forField(
                            target,
                            fieldTarget,
                            JvFieldTargetContext.of(currentFieldValue, oldFieldValue),
                            this
                    ))
            );
            oldFieldValues.put(fieldTarget.name(), currentFieldValue);
        }
    }

    private void initFields() throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        for (JvSubTarget fieldTarget : fieldTargets) {
            Field field = targetClass.getDeclaredField(fieldTarget.name());
            field.setAccessible(true);
            oldFieldValues.put(fieldTarget.name(), field.get(this.target));
        }
    }

    private boolean needToHandle(Object currentFieldValue, Object oldFieldValue) {
        return !Objects.equals(oldFieldValue, currentFieldValue);
    }
}
