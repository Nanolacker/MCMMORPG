package com.mcmmorpg.impl;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.MMORPGPlugin;

public class Main extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("starting");
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("stopping");
	}

}
