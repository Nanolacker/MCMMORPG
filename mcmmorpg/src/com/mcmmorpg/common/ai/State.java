package com.mcmmorpg.common.ai;

public abstract class State {
    protected abstract void initialize(StateMachine stateMachine);

    protected abstract void update(StateMachine stateMachine);

    protected abstract void terminate(StateMachine stateMachine);
}
