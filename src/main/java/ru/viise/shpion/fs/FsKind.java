package ru.viise.shpion.fs;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.*;

public enum FsKind {
        CREATE,
        DELETE,
        MODIFY,
        OVERFLOW;

        static WatchEvent.Kind<?> toWatchEventKind(FsKind kind) {
            return switch (kind) {
                case CREATE -> ENTRY_CREATE;
                case DELETE -> ENTRY_DELETE;
                case MODIFY -> ENTRY_MODIFY;
                case OVERFLOW -> StandardWatchEventKinds.OVERFLOW;
            };
        }

        static FsKind toKind(WatchEvent.Kind<?> watchEventKind) {
            if (watchEventKind == ENTRY_CREATE) {
                return CREATE;
            }
            if (watchEventKind == ENTRY_DELETE) {
                return DELETE;
            }
            if (watchEventKind == ENTRY_MODIFY) {
                return MODIFY;
            }
            if (watchEventKind == StandardWatchEventKinds.OVERFLOW) {
                return OVERFLOW;
            }
            throw new IllegalArgumentException("Unknown watch event kind: " + watchEventKind.name());
        }
    }