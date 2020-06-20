package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.constants.Quests;

public class FlintonMayor extends StaticHuman {

	private static final int LEVEL = 5;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTI0MTM4MzI3MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3NWY1ZmNiMmJiYjI0YjBkMjVjYmQxM2Q5NDhiMTVlOGU5OWVkODAzOWY1YmJhOWQwNjI5MTNkNmQ3MzRiMSIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "i3HqwgnQIDowgQDnZoqQaOVa1FZ9U93wPI366jACqxh5+aRWR3ssmh5YD/WvmaBM3824590iA5Js9iT37uul/rDhujDstxg9gEhg7Ci4FSq8RJ/rpwqt04h2MrX/JU2ETzN4anxHAEj5C0+JULrn6Rh1IJq4zONYjNRkYNXmHIs4A4skMTGbepgnj8njGMTt7DWrsLK5d2kCRJMubwqcHlXcKXduwGYyh0OGAy4G/cbuj33fGi2w7yYssAYMrTefb6sP/e6Xo2W3QNxNZSwyye1UcKiE0RexPFSmb5LCXo1JTy63G9gnkT8MYXFVRNIK4PSF8WDuJrAJtxcBrIXAhnUW8lSe2RWfUsKhu4J3EoxgjH8UIW73do1x5AJWZEsJ6UORnley9pHHHwxDEQ6QL9Z1Eqx796cvX2wbrlFlqePEEa7owcoAl4T0aXl80t/Aqp6s/oOtEJrbYaij+KBeUtYJ4YaPWUqzxp2FF0Fptl+LG915qwb6EguCuteNe1t77BrGoi9B06/7yyf0pEKYMRb7lbForeHZGp79/lPUZ1dHJ0QC+5gUunOV8Kv37kfIylNTm0x04Y+Ovsok4D6FyVOTO2k+3VJgU+Uj2sZ6zwn3PZKO8JNpLGo2Yrt+DcvXlE95Y31XJ9uD4UWvYgPy3KtNhRs5Fm7ujmryX3N62xQ=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final int CLEARING_THE_ROAD_XP_REWARD = 200;
	private static final int THREAT_LEVEL_GOD_XP_REWARD = 500;

	private final InteractionSequence completeClearingTheRoadInteraction;
	private final InteractionSequence completeThreatLevelGodInteraction;

	public FlintonMayor(Location location) {
		super(ChatColor.GREEN + "Mayor of Flinton", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(location.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerDisplayType getDisplayType(PlayerCharacter pc) {
				return QuestMarkerDisplayType.HIDDEN;
			}
		};
		completeClearingTheRoadInteraction = new InteractionSequence(11) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Welcome, adventurer. You look rather battered.", pc);
					break;
				case 1:
					say("You've cleared the paths to Melcher of the highwaymen? In that case you have Flinton's sincerest thanks.",
							pc);
					break;
				case 2:
					say("Unfortunately, the thieving filth runs deeper than the road.", pc);
					break;
				case 3:
					say("We've received reports from guards that bandits have begun establishing hideouts in the sewers directly below us.",
							pc);
					break;
				case 4:
					say("The reports suggest that they seem to be aiding a mysterious cult.", pc);
					break;
				case 5:
					say("We've sent in other guards to survey the area further but with little success.", pc);
					break;
				case 6:
					say("We could really use someone skilled like you to investigate the situation further.", pc);
					break;
				case 7:
					say("A small guard's post has been set up in the sewers. Go there and speak with Captain Nadia for further instructions.",
							pc);
					break;
				case 8:
					say("For the safety of Flinton, we cannot allow the cult's activities to go unmonitored any longer.",
							pc);
					break;
				case 9:
					say("Be careful, adventurer. We don't know what we're up against yet.", pc);
					break;
				case 10:
					Quests.CLEARING_THE_ROAD.getObjective(1).complete(pc);
					pc.giveXp(CLEARING_THE_ROAD_XP_REWARD);
					Quests.INTO_THE_SEWERS.start(pc);
					break;
				}
			}
		};
		completeThreatLevelGodInteraction = new InteractionSequence(8) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("By the gods, you found some lunatic who thinks he can become the new god of death?", pc);
					break;
				case 1:
					say("That genocidal maniac... The amount of sacrifices that it would take to reach such power is unspeakable.",
							pc);
					break;
				case 2:
					say("And you say that he already seemed to pose a formidable threat?", pc);
					break;
				case 3:
					say("This is truly worrisome. I must organize with other settlements to prepare for the possible destruction.",
							pc);
					break;
				case 4:
					say("Although, you came back in one piece. Perhaps you'd be able to keep track of his whereabouts.",
							pc);
					break;
				case 5:
					say("Traveller, you must venture out and stop Xylphanos at all costs. For the sake of all things living.",
							pc);
					break;
				case 6:
					Quests.THREAT_LEVEL_GOD.getObjective(0).complete(pc);
					pc.giveXp(THREAT_LEVEL_GOD_XP_REWARD);
					break;
				case 7:
					pc.sendMessage(ChatColor.GREEN
							+ "Thank you for playing the MCMMORPG demo! More content is planned the future.");
					break;
				}
			}
		};
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.CLEARING_THE_ROAD.compareStatus(pc, QuestStatus.IN_PROGRESS)
				&& Quests.CLEARING_THE_ROAD.getObjective(0).isComplete(pc)) {
			completeClearingTheRoadInteraction.advance(pc);
		} else if (Quests.THREAT_LEVEL_GOD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			completeThreatLevelGodInteraction.advance(pc);
		} else {
			say("Welcome to Flinton.", pc);
		}
	}

}
