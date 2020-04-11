package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Quests;

public class OakshireQuestGiver extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	protected OakshireQuestGiver(Location location) {
		super(ChatColor.GREEN + "Insert Name", 10, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.TO_THE_AID_OF_OAKSHIRE.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			SPEAK_NOISE.play(pc);
			Quests.REMOVING_THE_BANDITS.start(pc);
			Quests.RESCUING_THE_RESIDENTS.start(pc);
		}
	}

}
