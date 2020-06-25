package com.mcmmorpg.impl.load;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.impl.EntityListener;
import com.mcmmorpg.impl.ItemListener;
import com.mcmmorpg.impl.locations.FlintonListener;
import com.mcmmorpg.impl.locations.FlintonSewersListener;
import com.mcmmorpg.impl.locations.ForestListener;
import com.mcmmorpg.impl.locations.MelcherListener;
import com.mcmmorpg.impl.locations.PlainsListener;
import com.mcmmorpg.impl.playerCharacterSelection.PlayerCharacterSelectionListener;
import com.mcmmorpg.impl.playerClasses.FighterListener;
import com.mcmmorpg.impl.playerClasses.MageListener;

/**
 * Class for registering events for the plugin.
 */
public class ListenerLoader {

	/**
	 * Registers events for the plugin.
	 */
	public static void loadListeners() {
		EventManager.registerEvents(new PlayerCharacterSelectionListener());
		EventManager.registerEvents(new ItemListener());
		EventManager.registerEvents(new EntityListener());

		EventManager.registerEvents(new FighterListener());
		EventManager.registerEvents(new MageListener());

		EventManager.registerEvents(new MelcherListener());
		EventManager.registerEvents(new ForestListener());
		EventManager.registerEvents(new FlintonListener());
		EventManager.registerEvents(new FlintonSewersListener());
		EventManager.registerEvents(new PlainsListener());
	}

}
