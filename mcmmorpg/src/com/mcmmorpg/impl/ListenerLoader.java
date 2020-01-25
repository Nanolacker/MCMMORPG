package com.mcmmorpg.impl;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.listeners.PlayerCharacterSelectionListener;

public class ListenerLoader {

	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		
	}

}
