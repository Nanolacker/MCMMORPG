package com.mcmmorpg.common.ai;

public abstract class Behavior {
    private BehaviorStatus status;

    public Behavior() {
        this.status = BehaviorStatus.UNINITIALIZED;
    }

    public abstract void initialize();

    public abstract BehaviorStatus update();

    public abstract BehaviorStatus terminate(BehaviorStatus status);

    BehaviorStatus tick() {
        if (status == BehaviorStatus.UNINITIALIZED) {
            initialize();
        }
        status = update();
        if (status != BehaviorStatus.RUNNING) {
            terminate(status);
        }
        return status;
    }

    public BehaviorStatus getStatus() {
        return status;
    }
}
