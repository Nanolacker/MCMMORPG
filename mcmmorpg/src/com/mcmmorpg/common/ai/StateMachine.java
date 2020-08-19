package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import com.mcmmorpg.common.time.RepeatingTask;

public class StateMachine {

	private final List<State> states;
	private RepeatingTask updateTask;
	private int currentStateIndex;
	private int previousStateIndex;

	public StateMachine(double updatePeriod) {
		states = new ArrayList<>();
		updateTask = new RepeatingTask(updatePeriod) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	public void start(int stateIndex) {
		this.currentStateIndex = stateIndex;
		if (!states.isEmpty()) {
			states.get(stateIndex).initialize(this);
		}
		updateTask.schedule();
	}

	private void update() {
		State currentState = states.get(currentStateIndex);
		if (currentStateIndex != previousStateIndex) {
			State previousState = states.get(previousStateIndex);
			previousState.terminate(this);
			currentState.initialize(this);
		}
		currentState.update(this);
		previousStateIndex = currentStateIndex;
	}

	public double getUpdatePeriod() {
		return updateTask.getPeriod();
	}

	public void setUpdatePeriod(double updatePeriod) {
		updateTask.setPeriod(updatePeriod);
	}

	public void stop() {
		if (!states.isEmpty()) {
			states.get(0).terminate(this);
		}
		updateTask.cancel();
	}

	public void addState(int stateIndex, State state) {
		states.add(stateIndex, state);
	}

	public void setState(int stateIndex) {
		this.currentStateIndex = stateIndex;
	}

}
