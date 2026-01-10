package ru.viise.shpion;

import ru.viise.shpion.java.SpyJvFields;
import ru.viise.shpion.java.SpyOptionsJv;
import ru.viise.shpion.java.SpyWatcherJvFields;

import java.time.Duration;

public class SpyJavaFieldTest {

    static void main() throws InterruptedException {
        User user = new User();
        user.age = 1L;

        Spy<Void> spy = new SpyJvFields(
                new SpyOptionsJv().needPool(Duration.ofMillis(100L)),
                new SpyWatcherJvFields(user)
                        .from(
                                "name",
                                jvFieldEventContext -> IO.println(
                                        "CHANGE: field=" + jvFieldEventContext.fieldName()
                                                + ", oldValue=" + jvFieldEventContext.oldValue()
                                                + ", newValue=" + jvFieldEventContext.newValue()
                                )
                        )
                        .from(
                                "age",
                                jvFieldEventContext -> IO.println(
                                        "CHANGE: field=" + jvFieldEventContext.fieldName()
                                                + ", oldValue=" + jvFieldEventContext.oldValue()
                                                + ", newValue=" + jvFieldEventContext.newValue()
                                )
                        )
        );

        Thread thread = new Thread(spy::watch);
        thread.start();

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500L);
                ++user.age;
                user.name = "John_" + user.age;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        user.name = null;
        Thread.sleep(500L);
        user.age = user.age;
        Thread.sleep(500L);
        user.name = null;
        Thread.sleep(500L);
        user.age++;
        Thread.sleep(500L);
        spy.stop();

        thread.join();
    }

    public static class User {
        public String name;
        public String email;
        public Long age;

        public String sayHi(String yourName) {
            return "Hi, " + yourName + "!";
        }
    }
}
