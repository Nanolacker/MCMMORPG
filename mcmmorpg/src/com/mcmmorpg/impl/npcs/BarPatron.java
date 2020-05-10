package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.impl.Quests;

public class BarPatron extends AbstractFriendlyHuman {

	private static final int LEVEL = 2;
	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	protected BarPatron(Location location) {
		super(ChatColor.GREEN + "Bar Patron", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.CALMING_THE_TAVERN.compareStatus(pc, QuestStatus.COMPLETED)) {
			say("What the hell! You killed him!", pc);
		} else {
			say("I'm just trying to enjoy a drink.", pc);
		}
	}

}
