package ru.viise.shpion;

public interface Spy<SPY_OBJ> extends SpySelf {
    SPY_OBJ watch();
}
