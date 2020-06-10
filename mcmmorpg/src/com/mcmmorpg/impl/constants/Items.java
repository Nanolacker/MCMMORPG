package com.mcmmorpg.impl.constants;

import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;

/**
 * Contains references to all items that are loaded from the plugin's data
 * folder for convenience.
 */
public class Items {

	/**
	 * Starting fighter weapon.
	 */
	public static final Weapon APPRENTICE_SWORD = (Weapon) Item.forName("Apprentice Sword");
	/**
	 * Starting mage weapon.
	 */
	public static final Weapon APPRENTICE_STAFF = (Weapon) Item.forName("Apprentice Staff");
	/**
	 * Fighter weapon rewarded for completing the Arachnophobira quest as a fighter.
	 */
	public static final Weapon SPEAR_OF_THE_MELCHER_GUARD = (Weapon) Item.forName("Spear of the Melcher Guard");
	/**
	 * Mage weapon rewarded for completing the Arachnophobia quest as a mage.
	 */
	public static final Weapon STAFF_OF_THE_MELCHER_GUARD = (Weapon) Item.forName("Staff of the Melcher Guard");

	/**
	 * Fighter weapon that drops from various types of outlaws such as thieves and
	 * highwaymen.
	 */
	public static final Weapon THIEF_DAGGER = (Weapon) Item.forName("Thief Dagger");
	/**
	 * Fighter head armor that drops from various types of outlaws such as
	 * highwaymen.
	 */
	public static final ArmorItem HIDE_HEADGEAR = (ArmorItem) Item.forName("Hide Headgear");
	/**
	 * Fighter chest armor that drops from various types of outlaws such as
	 * highwaymen.
	 */
	public static final ArmorItem HIDE_TUNIC = (ArmorItem) Item.forName("Hide Tunic");
	/**
	 * Fighter leg armor that drops from various types of outlaws such as
	 * highwaymen.
	 */
	public static final ArmorItem HIDE_LEGGINGS = (ArmorItem) Item.forName("Hide Leggings");
	/**
	 * Fighter feet armor that drops from various types of outlaws such as
	 * highwaymen.
	 */
	public static final ArmorItem HIDE_BOOTS = (ArmorItem) Item.forName("Hide Boots");

	/**
	 * Mage weapon that drops from various types of outlaws such as thieves and
	 * highwaymen.
	 */
	public static final Weapon BRITTLE_WAND = (Weapon) Item.forName("Brittle Wand");
	/**
	 * Mage head armor that drops from various types of outlaws such as highwaymen.
	 */
	public static final ArmorItem TORN_HOOD = (ArmorItem) Item.forName("Torn Hood");
	/**
	 * Mage chest armor that drops from various types of outlaws such as highwaymen.
	 */
	public static final ArmorItem TORN_ROBES = (ArmorItem) Item.forName("Torn Robes");
	/**
	 * Mage leg armor that drops from various types of outlaws such as highwaymen.
	 */
	public static final ArmorItem TORN_LEGGINGS = (ArmorItem) Item.forName("Torn Leggings");
	/**
	 * Mage feet armor that drops from various types of outlaws such as highwaymen.
	 */
	public static final ArmorItem TORN_SHOES = (ArmorItem) Item.forName("Torn Shoes");

	/**
	 * Fighter weapon that drops from bandits.
	 */
	public static final Weapon BANDITS_BATTLE_AXE = (Weapon) Item.forName("Bandit's Battle Axe");
	/**
	 * Fighter head armor that drops from bandits.
	 */
	public static final ArmorItem BATTERED_MAIL_HELMET = (ArmorItem) Item.forName("Battered Mail Helmet");
	/**
	 * Fighter chest armor that drops from bandits.
	 */
	public static final ArmorItem BATTERED_MAIL_CUIRASS = (ArmorItem) Item.forName("Battered Mail Cuirass");
	/**
	 * Fighter leg armor that drops from bandits.
	 */
	public static final ArmorItem BATTERED_MAIL_GREAVES = (ArmorItem) Item.forName("Battered Mail Greaves");
	/**
	 * Fighter feet armor that drops from bandits.
	 */
	public static final ArmorItem BATTERED_MAIL_BOOTS = (ArmorItem) Item.forName("Battered Mail Boots");

	/**
	 * Fighter weapon that drops from cultists.
	 */
	public static final Weapon SKELETAL_WAND = (Weapon) Item.forName("Skeletal Wand");
	/**
	 * Mage head armor that drops from cultists.
	 */
	public static final ArmorItem CONJURERS_HOOD = (ArmorItem) Item.forName("Conjurer's Hood");
	/**
	 * Mage chest armor that drops from cultists.
	 */
	public static final ArmorItem CONJURERS_CLOAK = (ArmorItem) Item.forName("Conjurer's Cloak");
	/**
	 * Mage leg armor that drops from cultists.
	 */
	public static final ArmorItem CONJURERS_LEGGINGS = (ArmorItem) Item.forName("Conjurer's Leggings");
	/**
	 * Mage feet armor that drops from cultists.
	 */
	public static final ArmorItem CONJURERS_SHOES = (ArmorItem) Item.forName("Conjurer's Shoes");

	/**
	 * Quest item dropped by thieves.
	 */
	public static final Item STOLEN_FOOD = Item.forName("Stolen Food");
	/**
	 * Quest item dropped by gelatinous cubes.
	 */
	public static final Item SLUDGE = Item.forName("Sludge");
	/**
	 * Quest item dropped by the colossal gelatinous cube.
	 */
	public static final Item COLOSSAL_SLUDGE = Item.forName("Colossal Sludge");
	/**
	 * Quest item dropped by wild boars.
	 */
	public static final Item BOAR_FLANK = Item.forName("Boar Flank");

	/**
	 * Key required to open the west portcullis in Flinton Sewers.
	 */
	public static final Item WEST_SEWERS_KEY = Item.forName("West Sewers Key");
	/**
	 * Key required to open the north portcullis in Flinton Sewers.
	 */
	public static final Item NORTH_SEWERS_KEY = Item.forName("North Sewers Key");
	/**
	 * Key required to open the east portcullis in Flinton Sewers.
	 */
	public static final Item EAST_SEWERS_KEY = Item.forName("East Sewers Key");

	/**
	 * Food that restores health.
	 */
	public static final ConsumableItem STALE_BREAD = (ConsumableItem) Item.forName("Stale Bread");
	/**
	 * Food that restores health.
	 */
	public static final ConsumableItem GARLIC_BREAD = (ConsumableItem) Item.forName("Garlic Bread");
	/**
	 * Level 5 healing potion.
	 */
	public static final ConsumableItem POTION_OF_MINOR_HEALING = (ConsumableItem) Item
			.forName("Potion of Minor Healing");
	/**
	 * Level 10 healing potion.
	 */
	public static final ConsumableItem POTION_OF_LESSER_HEALING = (ConsumableItem) Item
			.forName("Potion of Lesser Healing");
	/**
	 * Level 15 healing potion.
	 */
	public static final ConsumableItem POTION_OF_HEALING = (ConsumableItem) Item.forName("Potion of Healing");
	/**
	 * Level 20 healing potion.
	 */
	public static final ConsumableItem POTION_OF_GREATER_HEALING = (ConsumableItem) Item
			.forName("Potion of Greater Healing");
	/**
	 * Consumable that makes player characters drunk.
	 */
	public static final ConsumableItem MELCHER_MEAD = (ConsumableItem) Item.forName("Melcher Mead");

}
