package com.mcmmorpg.impl;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.locations.BulskanRuinsListener;
import com.mcmmorpg.impl.locations.MelcherListener;
import com.mcmmorpg.impl.playerCharacterSelection.PlayerCharacterSelectionListener;
import com.mcmmorpg.impl.playerClasses.FighterListener;
import com.mcmmorpg.impl.playerClasses.MageListener;

public class ListenerLoader {

	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		EventManager.registerEvents(new FighterListener());
		EventManager.registerEvents(new MageListener());
		EventManager.registerEvents(new MelcherListener());
		EventManager.registerEvents(new BulskanRuinsListener());
	}

}
