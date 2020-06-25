package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.constants.Quests;

/**
 * Quest-giver in Melcher.
 */
public class MelcherMayor extends StaticHuman {

	private static final int LEVEL = 5;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTI0MTM4MzI3MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3NWY1ZmNiMmJiYjI0YjBkMjVjYmQxM2Q5NDhiMTVlOGU5OWVkODAzOWY1YmJhOWQwNjI5MTNkNmQ3MzRiMSIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "i3HqwgnQIDowgQDnZoqQaOVa1FZ9U93wPI366jACqxh5+aRWR3ssmh5YD/WvmaBM3824590iA5Js9iT37uul/rDhujDstxg9gEhg7Ci4FSq8RJ/rpwqt04h2MrX/JU2ETzN4anxHAEj5C0+JULrn6Rh1IJq4zONYjNRkYNXmHIs4A4skMTGbepgnj8njGMTt7DWrsLK5d2kCRJMubwqcHlXcKXduwGYyh0OGAy4G/cbuj33fGi2w7yYssAYMrTefb6sP/e6Xo2W3QNxNZSwyye1UcKiE0RexPFSmb5LCXo1JTy63G9gnkT8MYXFVRNIK4PSF8WDuJrAJtxcBrIXAhnUW8lSe2RWfUsKhu4J3EoxgjH8UIW73do1x5AJWZEsJ6UORnley9pHHHwxDEQ6QL9Z1Eqx796cvX2wbrlFlqePEEa7owcoAl4T0aXl80t/Aqp6s/oOtEJrbYaij+KBeUtYJ4YaPWUqzxp2FF0Fptl+LG915qwb6EguCuteNe1t77BrGoi9B06/7yyf0pEKYMRb7lbForeHZGp79/lPUZ1dHJ0QC+5gUunOV8Kv37kfIylNTm0x04Y+Ovsok4D6FyVOTO2k+3VJgU+Uj2sZ6zwn3PZKO8JNpLGo2Yrt+DcvXlE95Y31XJ9uD4UWvYgPy3KtNhRs5Fm7ujmryX3N62xQ=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final int REPORTING_FOR_DUTY_XP_REWARD = 50;
	private static final int THWARTING_THE_THIEVES_XP_REWARD = 125;

	private final InteractionSequence completeReportingForDutyInteraction;
	private final InteractionSequence completeThwartingTheThievesInteraction;

	public MelcherMayor(Location location) {
		super(ChatColor.GREEN + "Mayor of Melcher", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		completeReportingForDutyInteraction = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("Greetings adventurer!", pc);
					break;
				case 1:
					speak("You must have recently arrived by ship! Welcome to the land of Eladrador!", pc);
					break;
				case 2:
					speak("We've been in great need of adventurers like you lately. Bandits, cultists, and undead have been popping up like wildfire lately.",
							pc);
					break;
				case 3:
					speak("We'd all be greatful if you could help us restore peace to this land. Speak with me again when you're ready to support our cause.",
							pc);
					break;
				case 4:
					speak("To start, thieves that live in the woods around Melcher have been stealing food from the people of Melcher. Our people are going to starve if we don't do something.",
							pc);
					break;
				case 5:
					speak("I want you to find their hideout and teach them a lesson. Their crimes will not go unpunished!",
							pc);
					break;
				case 6:
					Quests.REPORTING_FOR_DUTY.getObjective(0).complete(pc);
					pc.giveXp(REPORTING_FOR_DUTY_XP_REWARD);
					Quests.THWARTING_THE_THIEVES.start(pc);
					Quests.THWARTING_THE_THIEVES.getObjective(0).setAccessible(pc, true);
					break;
				}
			}
		};
		completeThwartingTheThievesInteraction = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("My people are indebted to you. Thanks to you, this town will have a steady supply of food.",
							pc);
					break;
				case 1:
					speak("I have one last task for you, however.", pc);
					break;
				case 2:
					speak("Highwaymen have been ambushing travellers along the road east of Melcher.", pc);
					break;
				case 3:
					speak("They've been murdering and looting traders who visit Melcher.", pc);
					break;
				case 4:
					speak("They must be dealt with at once. Please take care of them for me.", pc);
					break;
				case 5:
					speak("When you're done, speak with the mayor of Flinton to inform him that the roads have been made safer.",
							pc);
					break;
				case 6:
					Quests.THWARTING_THE_THIEVES.getObjective(1).complete(pc);
					pc.giveXp(THWARTING_THE_THIEVES_XP_REWARD);
					Quests.CLEARING_THE_ROAD.start(pc);
					Quests.CLEARING_THE_ROAD.getObjective(0).setAccessible(pc, true);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			completeReportingForDutyInteraction.advance(pc);
		} else if (Quests.THWARTING_THE_THIEVES.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.THWARTING_THE_THIEVES.getObjective(0).isComplete(pc)) {
				completeThwartingTheThievesInteraction.advance(pc);
			} else {
				speak("Please teach those thieves a lesson for us, adventurer.", pc);
			}
		} else {
			speak("Greetings adventurer!", pc);
		}
	}

	@Override
	public void speak(String dialogue, PlayerCharacter recipient) {
		super.speak(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
