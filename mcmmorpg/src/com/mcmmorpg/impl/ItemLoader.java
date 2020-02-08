package com.mcmmorpg.impl;

import java.io.File;

import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.utils.IOUtils;

public class ItemLoader {

	public static void loadItems() {
		File baseItemsFolder = new File(IOUtils.getDataFolder(), "resources\\items");

		File simpleItemsFolder = new File(baseItemsFolder, "simple");
		File consumablesFolder = new File(baseItemsFolder, "consumables");
		File armorFolder = new File(baseItemsFolder, "armor");
		File weaponsFolder = new File(baseItemsFolder, "weapons");

		File[] weaponFiles = weaponsFolder.listFiles();
		for (File file : weaponFiles) {
			Weapon weapon = IOUtils.objectFromJsonFile(file, Weapon.class);
			weapon.initialize();
		}
	}

}
