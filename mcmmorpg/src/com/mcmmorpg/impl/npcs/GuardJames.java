package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

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

	private final InteractionSequence turnInInteraction;
	private final InteractionSequence completeInteraction;

	public GuardJames(Location location) {
		super(ChatColor.GREEN + "Guard James", 15, location, TEXTURE_DATA, TEXTURE_SIGNATURE);

		turnInInteraction = new InteractionSequence(1, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					Quests.REPORTING_FOR_DUTY.getObjective(0).complete(pc);
				} else {
					SPEAK_NOISE.play(getLocation());
				}
			}
		};
		completeInteraction = new InteractionSequence(1, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				SPEAK_NOISE.play(getLocation());
			}
		};

		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (!Quests.REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.COMPLETED)) {
			completeInteraction.advance(pc);
		} else {
			turnInInteraction.advance(pc);
		}
	}

}
