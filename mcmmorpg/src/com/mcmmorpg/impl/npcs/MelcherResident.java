package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class MelcherResident extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final String[] DIALOGUE_OPTIONS = { "Greetings, adventurer.", "How's it going?", "What do you need?",
			"Can I help you?", "I hear the bandits have been causing trouble." };
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	public MelcherResident(Location location) {
		super(ChatColor.GREEN + "Resident", 1, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		say(dialogue, pc);
		SPEAK_NOISE.play(pc);
	}

}
