package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.impl.Quests;

public class MelcherBartender extends StaticHuman {

	private static final int LEVEL = 2;
	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	protected MelcherBartender(Location location) {
		super(ChatColor.GREEN + "Bartender", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		QuestStatus status = Quests.CALMING_THE_TAVERN.getStatus(pc);
		switch (status) {
		case COMPLETED:
			say("Thanks for the help.", pc);
		case IN_PROGRESS:
			if (Quests.CALMING_THE_TAVERN.getObjective(0).isComplete(pc)) {
				Quests.CALMING_THE_TAVERN.getObjective(1).complete(pc);
			}
			break;
		case NOT_STARTED:
			say("Help.", pc);
			Quests.CALMING_THE_TAVERN.start(pc);
		default:
			break;
		}
	}

}
