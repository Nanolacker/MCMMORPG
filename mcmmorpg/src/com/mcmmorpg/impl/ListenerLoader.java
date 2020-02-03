package com.mcmmorpg.impl;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.listeners.BulskanRuinsListener;
import com.mcmmorpg.impl.listeners.FighterListener;
import com.mcmmorpg.impl.listeners.MelcherListener;
import com.mcmmorpg.impl.listeners.PlayerCharacterSelectionListener;

public class ListenerLoader {

	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		EventManager.registerEvents(new FighterListener());
		EventManager.registerEvents(new BulskanRuinsListener());
		EventManager.registerEvents(new MelcherListener());
	}

}
