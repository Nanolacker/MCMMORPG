package com.mcmmorpg.impl;

import java.io.File;

import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.utils.IOUtils;

public class ItemLoader {

	public static void loadItems() {
		File baseItemsFolder = new File(IOUtils.getDataFolder(), "resources\\items");

		File simpleItemsFolder = new File(baseItemsFolder, "simple");
		File consumablesFolder = new File(baseItemsFolder, "consumables");
		File armorFolder = new File(baseItemsFolder, "armor");
		File weaponsFolder = new File(baseItemsFolder, "weapons");

		File[] simpleItemFiles = simpleItemsFolder.listFiles();
		for (File file : simpleItemFiles) {
			Item item = IOUtils.readJson(file, Item.class);
			item.initialize();
		}
		File[] consumablesFiles = consumablesFolder.listFiles();
		for (File file : consumablesFiles) {
			ConsumableItem consumable = IOUtils.readJson(file, ConsumableItem.class);
			consumable.initialize();
		}
		File[] armorFiles = armorFolder.listFiles();
		for (File file : armorFiles) {
			ArmorItem armor = IOUtils.readJson(file, ArmorItem.class);
			armor.initialize();
		}
		File[] weaponFiles = weaponsFolder.listFiles();
		for (File file : weaponFiles) {
			Weapon weapon = IOUtils.readJson(file, Weapon.class);
			weapon.initialize();
		}
	}

}
