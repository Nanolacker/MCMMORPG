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

public class FlintonMayor extends StaticHuman {

	private static final int LEVEL = 5;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTI0MTM4MzI3MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3NWY1ZmNiMmJiYjI0YjBkMjVjYmQxM2Q5NDhiMTVlOGU5OWVkODAzOWY1YmJhOWQwNjI5MTNkNmQ3MzRiMSIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "i3HqwgnQIDowgQDnZoqQaOVa1FZ9U93wPI366jACqxh5+aRWR3ssmh5YD/WvmaBM3824590iA5Js9iT37uul/rDhujDstxg9gEhg7Ci4FSq8RJ/rpwqt04h2MrX/JU2ETzN4anxHAEj5C0+JULrn6Rh1IJq4zONYjNRkYNXmHIs4A4skMTGbepgnj8njGMTt7DWrsLK5d2kCRJMubwqcHlXcKXduwGYyh0OGAy4G/cbuj33fGi2w7yYssAYMrTefb6sP/e6Xo2W3QNxNZSwyye1UcKiE0RexPFSmb5LCXo1JTy63G9gnkT8MYXFVRNIK4PSF8WDuJrAJtxcBrIXAhnUW8lSe2RWfUsKhu4J3EoxgjH8UIW73do1x5AJWZEsJ6UORnley9pHHHwxDEQ6QL9Z1Eqx796cvX2wbrlFlqePEEa7owcoAl4T0aXl80t/Aqp6s/oOtEJrbYaij+KBeUtYJ4YaPWUqzxp2FF0Fptl+LG915qwb6EguCuteNe1t77BrGoi9B06/7yyf0pEKYMRb7lbForeHZGp79/lPUZ1dHJ0QC+5gUunOV8Kv37kfIylNTm0x04Y+Ovsok4D6FyVOTO2k+3VJgU+Uj2sZ6zwn3PZKO8JNpLGo2Yrt+DcvXlE95Y31XJ9uD4UWvYgPy3KtNhRs5Fm7ujmryX3N62xQ=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence completeClearingTheRoadInteraction;
	private final InteractionSequence startIntoTheSewersInteraction;

	public FlintonMayor(Location location) {
		super(ChatColor.GREEN + "Mayor of Flinton", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
		completeClearingTheRoadInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Insert dialogue", pc);
					break;
				case 1:
					Quests.CLEARING_THE_ROAD.getObjective(1).complete(pc);
					break;
				}
			}
		};
		startIntoTheSewersInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Insert dialogue", pc);
					break;
				case 1:
					Quests.INTO_THE_SEWERS.start(pc);
					break;
				}
			}
		};
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.CLEARING_THE_ROAD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.CLEARING_THE_ROAD.getObjective(0).isComplete(pc)) {
				completeClearingTheRoadInteraction.advance(pc);
			} else {
				say("Go slay highwaymen.", pc);
			}
		} else if (Quests.CLEARING_THE_ROAD.compareStatus(pc, QuestStatus.COMPLETED)
				&& Quests.INTO_THE_SEWERS.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startIntoTheSewersInteraction.advance(pc);
		}
	}

}
