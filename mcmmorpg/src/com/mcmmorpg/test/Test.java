package com.mcmmorpg.test;

import org.bukkit.Material;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.ItemRarity;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.util.Debug;

public class Test extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		DeveloperCommands.registerDeveloperCommands();
		EventManager.registerEvents(new TestListener());
		Debug.log(Constants.TEST_SPAWN_LOCATION);
		new AiTestNpc(Constants.TEST_SPAWN_LOCATION).setAlive(true);
		ConsumableItem consumable = new ConsumableItem("Test Consumable", ItemRarity.EPIC, Material.GLASS_BOTTLE, null,
				1);
		consumable.initialize();
		LootChest.spawnLootChest(Constants.TEST_SPAWN_LOCATION, consumable);
	}

	@Override
	protected void onMMORPGStop() {

	}

}
