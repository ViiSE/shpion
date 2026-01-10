package ru.viise.shpion.java;

import ru.viise.shpion.SpyOptionsGeneral;

import java.time.Duration;

@SuppressWarnings("ClassCanBeRecord")
public final class SpyOptionsJv {

    private final SpyOptionsGeneral optionsGeneral;

    SpyOptionsJv(SpyOptionsGeneral optionsGeneral) {
        this.optionsGeneral = optionsGeneral;
    }

    public SpyOptionsJv() {
        this(new SpyOptionsGeneral());
    }

    public SpyOptionsJv needPool(Duration polDur) {
        optionsGeneral.needPool(polDur);
        return this;
    }

    SpyOptionsGeneral optionsGeneral() {
        return optionsGeneral;
    }
}
