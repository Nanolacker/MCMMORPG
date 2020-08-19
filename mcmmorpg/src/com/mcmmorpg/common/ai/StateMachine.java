package com.mcmmorpg.common.ai;

import com.mcmmorpg.common.time.RepeatingTask;

public class StateMachine {

	private State currentState;
	private final RepeatingTask updateTask;

	public StateMachine(State initialState, double updatePeriod) {
		currentState = null;
		updateTask = new RepeatingTask(updatePeriod) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	public State getState() {
		return currentState;
	}

	public void setState(State state) {
		if (state == currentState) {
			return;
		}
		currentState.terminate(this);
		state.initialize(this);
		currentState = state;
	}

	private void update() {
		currentState.update(this);
	}

	public double getUpdatePeriod() {
		return updateTask.getPeriod();
	}

	public void setUpdatePeriod(double updatePeriod) {
		updateTask.setPeriod(updatePeriod);
	}

	public void start() {
		currentState.initialize(this);
		updateTask.schedule();
	}

	public void stop() {
		currentState.terminate(this);
		updateTask.cancel();
	}

}
