package com.mcmmorpg.impl.constants;

import com.mcmmorpg.common.quest.Quest;

/**
 * Contains references to all quests that are loaded from the plugin's data
 * folder for convenience.
 */
public class Quests {

	/**
	 * Quest that has the player slay thieves that have been causing trouble for the
	 * residents of Melcher.
	 */
	public static final Quest THWARTING_THE_THIEVES = Quest.forName("Thwarting the Thieves");
	/**
	 * Quest that has the player recover stolen food from thieves in the forest and
	 * return them to the farmer in Melcher.
	 */
	public static final Quest FOOD_DELIVERY = Quest.forName("Food Delivery");
	/**
	 * Quest that has the player slay highwaymen along the road between Melcher and
	 * Flinton and report to the mayor of Flinton.
	 */
	public static final Quest CLEARING_THE_ROAD = Quest.forName("Clearing the Road");
	/**
	 * First quest of the game that has the player report to the mayor of Melcher.
	 */
	public static final Quest REPORTING_FOR_DUTY = Quest.forName("Reporting for Duty");
	/**
	 * Quest that has the player get into a fight with an angered drunkard.
	 */
	public static final Quest BAR_FIGHT = Quest.forName("Bar Fight");
	/**
	 * Quest that has the player slay forest spiders and the Broodmother.
	 */
	public static final Quest ARACHNOPHOBIA = Quest.forName("Arachnophobia");
	/**
	 * Quest that has the player eliminate rats in the basement of the Melcher
	 * tavern.
	 */
	public static final Quest PEST_CONTROL = Quest.forName("Pest Control");
	/**
	 * Quest that has the player slay gelatinous cubes and return their ooze to the
	 * alchemist in Flinton Sewers.
	 */
	public static final Quest SAMPLING_SLUDGE = Quest.forName("Sampling Sludge");
	/**
	 * Quest that has the player report to guards in Flinton Sewers.
	 */
	public static final Quest INTO_THE_SEWERS = Quest.forName("Into the Sewers");
	/**
	 * Quest that has the player eliminate bandits in Flinton Sewers.
	 */
	public static final Quest DRIVING_OUT_THE_BANDITS = Quest.forName("Driving Out the Bandits");
	/**
	 * Quest that has the player eliminate cultists and fight Xylphanos in Flinton
	 * Sewers.
	 */
	public static final Quest CULLING_THE_CULT = Quest.forName("Culling the Cult");
	/**
	 * Quest that has the player report back to the mayor of Flinton after fighting
	 * Xylphanos.
	 */
	public static final Quest THREAT_LEVEL_GOD = Quest.forName("Threat Level God");
	/**
	 * Quest that has the player collect boar tusk for the master alchemist in
	 * Flinton.
	 */
	public static final Quest BOARS_GALORE = Quest.forName("Boars Galore");

}
