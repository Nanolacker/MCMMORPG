package com.mcmmorpg.common.ai;

/**
 * A composite behavior which enumerates each of its child behaviors until it
 * finds one that succeeds. Will fail if all child behaviors fail.
 */
public class Selector extends Composite {

	private int selector;

	@Override
	public void initialize() {
		selector = 0;
	}

	@Override
	public BehaviorStatus update() {
		while (true) {
			BehaviorStatus status = getChild(selector).tick();
			if (status != BehaviorStatus.FAILURE) {
				// status is running or success
				if (status == BehaviorStatus.SUCCESS) {
					selector = 0;
				}
				return status;
			}
			if (++selector == getChildCount()) {
				return BehaviorStatus.FAILURE;
			}
		}
	}

	@Override
	public BehaviorStatus terminate(BehaviorStatus status) {
		return null;
	}

}
