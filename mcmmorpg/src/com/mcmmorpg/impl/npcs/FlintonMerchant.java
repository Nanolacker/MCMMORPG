package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class FlintonMerchant extends StaticHuman {

	private static final String TEXTURE_DATA[] = {
			"ewogICJ0aW1lc3RhbXAiIDogMTU5MTgxODg1NzQyMiwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMDkyNjE1ZmRkYTA5OTA4YTk5YTU1ZGM3ODM0NmViYzEwNjExZmEyYjNlYzJlNjQzOTZhMWM4Mjc0NGQ0MWQzIgogICAgfQogIH0KfQ==",
			"ewogICJ0aW1lc3RhbXAiIDogMTU5MTgxODkxNzUxMSwKICAicHJvZmlsZUlkIiA6ICJhNzFjNTQ5MmQwNTE0ZDg3OGFiOTEwZmRmZmRmYzgyZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcHBsZTU0NDciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFiMjc4NWM3MGNkYjEwMTMzOWNmN2IzNjdkZmMxZDMxMThhMDhkMDQ1MzJiMWYwYTBhNzRmYzc1YWU1ZWYwOSIKICAgIH0KICB9Cn0=",
			"ewogICJ0aW1lc3RhbXAiIDogMTU5MTgxODk5Nzk1OSwKICAicHJvZmlsZUlkIiA6ICJmYjE4Zjk1YzgyODI0ZTNmODQyNTJkZjdjMDI4Mzc2MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbGVrYXlUaGVMaW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM5YjQ2NGM0M2IxNDcyOWYyNjg5MDc5M2JlYzZjMzZmZmJmZTM2NDJhMzY3OTA2YjliYTgzODA2MmNlM2U3YjYiCiAgICB9CiAgfQp9" };
	private static final String TEXTURE_SIGNATURE[] = {
			"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" };
	private static final String[] DIALOGUE_OPTIONS = { "Take your time browsing!", "I'm here to help.",
			"Anything catch your eye?", "Big sale today, adventurer.",
			"Beautiful day to buy some of my wares, isn't it?", "See something you like?",
			"Don't be shy, have a look around.", "I've got the best prices in Eladrador.",
			"How about a special offer, just for you?", "No shirt, no shoes, no service." };
	private static final Noise MALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final Noise FEMALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);
	boolean male;

	public FlintonMerchant(Location spawnLocation, int skinIndex) {
		super(ChatColor.GREEN + "Merchant", 1, spawnLocation, TEXTURE_DATA[skinIndex], TEXTURE_SIGNATURE[skinIndex]);
		male = skinIndex == 0 || skinIndex == 2;
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		speak(dialogue, pc);
		if (male) {
			MALE_SPEAK_NOISE.play(pc);
		} else {
			FEMALE_SPEAK_NOISE.play(pc);
		}
	}

}
