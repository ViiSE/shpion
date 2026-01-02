package ru.viise.shpion.fs;

import ru.viise.shpion.SpySelf;

import java.nio.file.Path;

public record FsEventContext(
        Path relativePath,
        Path absolutePath,
        FsEvent event,
        SpySelf self) { }
