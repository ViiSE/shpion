package ru.viise.shpion;

public class SpyRunException extends RuntimeException {

    public SpyRunException(String message) {
        super(message);
    }

    public SpyRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
