package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import com.mcmmorpg.common.time.RepeatingTask;

public class StateMachine {

	private static final double UPDATE_PERIOD = 0.1;

	private final List<State> states;
	private RepeatingTask updateTask;
	private int currentStateIndex;
	private int previousStateIndex;

	public StateMachine() {
		states = new ArrayList<>();
		updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	public void start(int state) {
		this.currentStateIndex = state;
		updateTask.schedule();
	}

	private void update() {
		State currentState = states.get(currentStateIndex);
		if (currentStateIndex != previousStateIndex) {
			State previousState = states.get(previousStateIndex);
			previousState.onStateExit(this);
			currentState.onStateEnter(this);
		}
		previousStateIndex = currentStateIndex;
	}

	public void stop() {
		updateTask.cancel();
	}

	public void addState(State state) {
		states.add(state);
	}

	public void setState(int state) {
		this.currentStateIndex = state;
	}

}
