package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;

public class FlintonQuestGiver extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	private static final Quest TO_THE_AID_OF_FLINTON = Quest.forName("To the Aid of Flinton");
	private static final Quest REMOVING_THE_BANDITS = Quest.forName("Removing the Bandits");
	private static final Quest RESCUING_THE_RESIDENTS = Quest.forName("Rescuing the Residents");

	protected FlintonQuestGiver(Location location) {
		super(ChatColor.GREEN + "Insert Name", 10, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (TO_THE_AID_OF_FLINTON.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			SPEAK_NOISE.play(pc);
			REMOVING_THE_BANDITS.start(pc);
			RESCUING_THE_RESIDENTS.start(pc);
		}
	}

}
