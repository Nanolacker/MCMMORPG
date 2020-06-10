package com.mcmmorpg.impl.resourceLoad;

import java.io.File;

import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Class for loading items from the plugin's data folder. The listeners for the
 * items are handled elsewhere.
 */
public class ItemLoader {

	/**
	 * Loads items from the plugin's data folder. The listeners for the items are
	 * handled elsewhere.
	 */
	public static void loadItems() {
		File baseItemsFolder = new File(IOUtility.getDataFolder(), "resources/items");

		File miscellaneousItemsFolder = new File(baseItemsFolder, "miscellaneous");
		File consumablesFolder = new File(baseItemsFolder, "consumables");
		File armorFolder = new File(baseItemsFolder, "armor");
		File weaponsFolder = new File(baseItemsFolder, "weapons");
		File[] miscellaneousItemFiles = miscellaneousItemsFolder.listFiles();
		for (File file : miscellaneousItemFiles) {
			Item item = IOUtility.readJson(file, Item.class);
			item.initialize();
		}
		File[] consumablesFiles = consumablesFolder.listFiles();
		for (File file : consumablesFiles) {
			ConsumableItem consumable = IOUtility.readJson(file, ConsumableItem.class);
			consumable.initialize();
		}
		File[] armorFiles = armorFolder.listFiles();
		for (File file : armorFiles) {
			ArmorItem armor = IOUtility.readJson(file, ArmorItem.class);
			armor.initialize();
		}
		File[] weaponFiles = weaponsFolder.listFiles();
		for (File file : weaponFiles) {
			Weapon weapon = IOUtility.readJson(file, Weapon.class);
			weapon.initialize();
		}
	}

}
