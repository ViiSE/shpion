package ru.viise.shpion;

import ru.viise.shpion.java.JvHandler;
import ru.viise.shpion.java.JvOptions;
import ru.viise.shpion.java.JvSubTarget;
import ru.viise.shpion.java.SpyJavaField;

import java.util.List;

public class SpyJavaFieldTest {

    static void main() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        User user = new User();
        user.age = 1L;

        Spy spy = new SpyJavaField(JvOptions.of(
                user,
                List.of(
                        JvSubTarget.field("name"),
                        JvSubTarget.field("age")
                ),
                List.of(JvHandler.forField(
                        jvEventContext -> IO.println(
                                "CHANGE: field=" + jvEventContext.subTarget().name()
                                        + ", oldValue=" + jvEventContext.fieldTargetContext().oldValue()
                                        + ", newValue=" + jvEventContext.fieldTargetContext().newValue()
                        )
                ))),
                true
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
