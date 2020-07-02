package com.mcmmorpg.common.ai;

public abstract class State {

	protected abstract void onStateEnter(StateMachine stateMachine);

	protected abstract void onStateUpdate(StateMachine stateMachine);

	protected abstract void onStateExit(StateMachine stateMachine);

}
