package ru.viise.shpion.java;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import ru.viise.shpion.Spy;
import ru.viise.shpion.SpyRunException;
import ru.viise.shpion.SpyTarget;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpyJvMethods<T_OBJ> implements Spy<T_OBJ> {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
//    private final ExecutorService executor;

    private final SpyOptionsJv options;
    private final SpyWatcherJvMethods<T_OBJ> watcher;

    public SpyJvMethods(SpyOptionsJv options, SpyWatcherJvMethods<T_OBJ> watcher) {
        this.options = options;
        this.watcher = watcher;
//        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public T_OBJ watch() {
        try {
            return createWatchableTarget();
        } catch (IllegalAccessException e) {
            throw new SpyRunException(e.getMessage(), e);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
    }

    @SuppressWarnings("unchecked")
    private T_OBJ createWatchableTarget() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> targetClass = watcher.watchObject().getClass();

        Class<?> dynamicType = new ByteBuddy()
                .subclass(targetClass)
                .method(ElementMatchers.isDeclaredBy(targetClass)
                        .and(ElementMatchers.namedOneOf(watcher.targets().stream()
                                .map(SpyTarget::target)
                                .toList()
                                .toArray(new String[0]))
                        ))
                .intercept(MethodDelegation.to(new JvMethodInterceptor(this, watcher)))
                .make()
                .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        T_OBJ watchableTarget = (T_OBJ) dynamicType.getDeclaredConstructor().newInstance();
        copyState(watcher.watchObject(), watchableTarget);

        return watchableTarget;
    }

    private void copyState(Object source, Object destination) throws IllegalAccessException {
        Class<?> sourceClass = source.getClass();
        while (sourceClass != null) {
            for (Field sourceField : sourceClass.getDeclaredFields()) {
                if (Modifier.isStatic(sourceField.getModifiers())) {
                    continue;
                }
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);
                sourceField.set(destination, value);
            }
            sourceClass = sourceClass.getSuperclass();
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class JvMethodInterceptor {

        private final Spy<?> self;
        private final SpyWatcherJvMethods<?> watcher;

        public JvMethodInterceptor(Spy<?> self, SpyWatcherJvMethods<?> watcher) {
            this.self = self;
            this.watcher = watcher;
        }

        @SuppressWarnings("unused")
        @RuntimeType
        public Object intercept(
                @Origin Method method,
                @AllArguments Object[] args,
                @SuperCall Callable<?> callable) throws Throwable {
            Throwable methodErr = null;
            Object result = null;
            try {
                result = callable.call();
            } catch (Throwable t) {
                methodErr = t;
                throw t;
            } finally {
                long handlerCount = watcher.targets().stream()
                        .filter(spyTarget -> spyTarget.target().equals(method.getName()))
                        .count();
                if (handlerCount > 0) {
                    JvMethodEventContext context = new JvMethodEventContext(
                            watcher.watchObject(),
                            args,
                            result,
                            methodErr,
                            self
                    );

                    watcher.targets().forEach(spyTarget -> {
                        if (spyTarget.target().equals(method.getName())) {
                            spyTarget.handlers().forEach(handler -> handler.accept(context));
                        }
                    });
                }
            }

            return result;
        }
    }
}
