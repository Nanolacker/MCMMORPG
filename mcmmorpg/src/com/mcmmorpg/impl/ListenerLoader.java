package com.mcmmorpg.impl;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.locationListeners.BulskanRuinsListener;
import com.mcmmorpg.impl.locationListeners.MelcherListener;
import com.mcmmorpg.impl.locationListeners.PlayerCharacterSelectionListener;
import com.mcmmorpg.impl.playerClassListeners.FighterListener;
import com.mcmmorpg.impl.playerClassListeners.MageListener;

public class ListenerLoader {

	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		EventManager.registerEvents(new FighterListener());
		EventManager.registerEvents(new MageListener());
		EventManager.registerEvents(new BulskanRuinsListener());
		EventManager.registerEvents(new MelcherListener());
	}

}
