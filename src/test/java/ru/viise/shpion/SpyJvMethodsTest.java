package ru.viise.shpion;

import ru.viise.shpion.java.SpyJvMethods;
import ru.viise.shpion.java.SpyOptionsJv;
import ru.viise.shpion.java.SpyWatcherJvMethods;

import java.util.Arrays;

public class SpyJvMethodsTest {

    static void main() {
        User user = new User();
        user.name = "John";

        Spy<User> spyMethods = new SpyJvMethods<>(
                new SpyOptionsJv(),
                new SpyWatcherJvMethods<>(user)
                        .from(
                                "sayHi",
                                jvMethodEventContext -> {
                                    System.out.println("HERE! args: " + Arrays.toString(jvMethodEventContext.args()));
                                }
                        )
        );

        User watchableUser = spyMethods.watch();
        watchableUser.sayHi("Lenin");
        watchableUser.sayGoodbye("Lenin");
    }

    public static class User {
        public String name;
        public String email;
        public Long age;

        public void sayHi(String yourName) {
        }

        public void sayGoodbye(String yourName) {
        }
    }
}
