package com.mcmmorpg.test;

import com.mcmmorpg.common.MMORPGPlugin;

public class Test extends MMORPGPlugin{

	@Override
	protected void onMMORPGStart() {
		DeveloperCommands.registerDeveloperCommands();
	}

	@Override
	protected void onMMORPGStop() {
		
	}

}
