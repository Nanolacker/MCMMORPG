package com.mcmmorpg.impl;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.locations.CrestfordGraveyardListener;
import com.mcmmorpg.impl.locations.CrestfordListener;
import com.mcmmorpg.impl.locations.FlintonListener;
import com.mcmmorpg.impl.locations.FlintonSewersListener;
import com.mcmmorpg.impl.locations.MelcherListener;
import com.mcmmorpg.impl.locations.OakshireListener;
import com.mcmmorpg.impl.playerCharacterSelection.PlayerCharacterSelectionListener;
import com.mcmmorpg.impl.playerClasses.FighterListener;
import com.mcmmorpg.impl.playerClasses.MageListener;

public class ListenerLoader {

	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		EventManager.registerEvents(new ItemListener());

		EventManager.registerEvents(new FighterListener());
		EventManager.registerEvents(new MageListener());

		EventManager.registerEvents(new MelcherListener());
		EventManager.registerEvents(new OakshireListener());
		EventManager.registerEvents(new CrestfordListener());
		EventManager.registerEvents(new FlintonSewersListener());
		EventManager.registerEvents(new CrestfordGraveyardListener());
		EventManager.registerEvents(new FlintonListener());
	}

}
