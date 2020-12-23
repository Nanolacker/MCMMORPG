package com.mcmmorpg.test;

import org.bukkit.Material;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.ItemRarity;
import com.mcmmorpg.common.item.LootChest;

public class Test extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		DeveloperCommands.registerDeveloperCommands();
		EventManager.registerEvents(new TestListener());
		new AiTestNpc(Constants.TEST_SPAWN_LOCATION.clone().add(0, 0, 5)).setAlive(true);
		ConsumableItem consumable = new ConsumableItem("Test Consumable", ItemRarity.EPIC, Material.GLASS_BOTTLE, null,
				1);
		consumable.initialize();
		LootChest.spawnLootChest(Constants.TEST_SPAWN_LOCATION, consumable);
	}

	@Override
	protected void onMMORPGStop() {

	}

}
