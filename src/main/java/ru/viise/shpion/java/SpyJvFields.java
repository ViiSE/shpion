package ru.viise.shpion.java;

import ru.viise.shpion.SpyPoolable;
import ru.viise.shpion.SpyRunException;
import ru.viise.shpion.SpyTarget;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpyJvFields implements SpyPoolable {

    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final SpyOptionsJv options;
    private final SpyWatcherJvFields watcher;

    private final Map<String, Optional<Object>> oldFieldValues = new ConcurrentHashMap<>();
    private final Map<String, Field> targetFields = new ConcurrentHashMap<>();

    public SpyJvFields(SpyOptionsJv options, SpyWatcherJvFields watcher) {
        this.options = options;
        this.watcher = watcher;
    }

    @Override
    public Void watch() {
        try {
            initFields();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SpyRunException(e.getMessage(), e);
        }

        if (options.optionsGeneral().needPool()) {
            watchInLoop();
        } else {
            iterateWatch();
        }

        return null;
    }

    @Override
    public void stop() {
        stopped.set(true);
    }

    private void initFields() throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = watcher.watchObject().getClass();
        for (SpyTarget<String, JvFieldEventContext> spyTarget : watcher.targets()) {
            Field field = targetClass.getDeclaredField(spyTarget.target());
            field.setAccessible(true);
            oldFieldValues.put(spyTarget.target(), Optional.ofNullable(field.get(watcher.watchObject())));
            targetFields.put(spyTarget.target(), field);
        }
    }

    @SuppressWarnings("BusyWait")
    private void watchInLoop() {
        try {
            while (!stopped.get()) {
                iterateWatch();
                Thread.sleep(options.optionsGeneral().pollDur().toMillis());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void iterateWatch() {
        try {
            for (SpyTarget<String, JvFieldEventContext> spyTarget : watcher.targets()) {
                checkFieldValue(spyTarget);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFieldValue(SpyTarget<String, JvFieldEventContext> spyTarget) throws NoSuchFieldException, IllegalAccessException {
        Field targetField = targetFields.get(spyTarget.target());

        Object currentFieldValue = targetField.get(watcher.watchObject());
        Object oldFieldValue = oldFieldValues.getOrDefault(spyTarget.target(), Optional.empty()).orElse(null);

        if (needToHandle(currentFieldValue, oldFieldValue)) {
            spyTarget.handlers().forEach(handler -> handler.accept(
                    new JvFieldEventContext(
                            watcher.watchObject(),
                            spyTarget.target(),
                            targetField,
                            currentFieldValue,
                            oldFieldValue,
                            this
                    ))
            );
            oldFieldValues.put(spyTarget.target(), Optional.ofNullable(currentFieldValue));
        }
    }

    private boolean needToHandle(Object currentFieldValue, Object oldFieldValue) {
        return !Objects.equals(oldFieldValue, currentFieldValue);
    }
}
