package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Quests;

/**
 * Quest-giver in Melcher.
 */
public class GuardJames extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence reportingForDutyCompleteInteraction;
	private final InteractionSequence startSlayingTheThievesInteraction;
	private final InteractionSequence completeSlayingTheThievesInteraction;

	public GuardJames(Location location) {
		super(ChatColor.GREEN + "Guard James", 15, location, TEXTURE_DATA, TEXTURE_SIGNATURE);

		reportingForDutyCompleteInteraction = new InteractionSequence(5) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Greetings adventurere!", pc);
					break;
				case 1:
					say("You must have recently arrived by ship! Welcome to the land of Eladrador!", pc);
					break;
				case 2:
					say("We've been in great need of adventurers like you lately. Bandits, cultists, and undead have been popping up like wildfire lately.",
							pc);
					break;
				case 3:
					say("We'd all be greatful if you could help us restore peace to this land. Speak with me again when you're ready to support our cause.",
							pc);
					break;
				case 4:
					Quests.REPORTING_FOR_DUTY.getObjective(0).complete(pc);
					break;
				}
			}
		};
		startSlayingTheThievesInteraction = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Thieves that live in the woods around Melcher have been stealing food from the people of Melcher. Our people are going to starve if we don't do something.",
							pc);
					break;
				case 1:
					say("I want you to find their hideout and slay them for us. Their crimes will not go unpunished!",
							pc);
					break;
				case 2:
					Quests.SLAYING_THE_THIEVES.start(pc);
					break;
				}
			}
		};
		completeSlayingTheThievesInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("My people are indebted to you. Thanks to you, this town will have a steady supply of food.",
							pc);
					break;
				case 1:
					Quests.SLAYING_THE_THIEVES.getObjective(1).complete(pc);
					break;
				}
			}
		};
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			reportingForDutyCompleteInteraction.advance(pc);
		} else if (Quests.REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.COMPLETED)
				&& Quests.SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startSlayingTheThievesInteraction.advance(pc);
		} else if (Quests.SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.SLAYING_THE_THIEVES.getObjective(0).isComplete(pc)) {
				completeSlayingTheThievesInteraction.advance(pc);
			} else {
				say("Please slay those thieves for us, adventurer.", pc);
			}
		} else {
			say("Greetings adventurer!", pc);
		}
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

}
