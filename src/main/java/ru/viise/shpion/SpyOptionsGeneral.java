package ru.viise.shpion;

import java.time.Duration;

public final class SpyOptionsGeneral {

    private boolean needPool;
    private Duration pollDur;

    SpyOptionsGeneral(boolean needPool, Duration pollDur) {
        this.needPool = needPool;
        this.pollDur = pollDur;
    }

    public SpyOptionsGeneral() {
        this(false, Duration.ZERO);
    }

    public boolean needPool() {
        return needPool;
    }

    public Duration pollDur() {
        return pollDur;
    }

    public void needPool(Duration pollDur) {
        this.needPool = true;
        this.pollDur = pollDur;
    }
}
