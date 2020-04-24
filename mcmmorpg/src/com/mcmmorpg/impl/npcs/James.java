package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.common.ui.Notice;
import com.mcmmorpg.common.ui.Notice.NoticeType;
import com.mcmmorpg.impl.Quests;

/**
 * Quest-giver in Melcher.
 */
public class James extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence turnInSequence;
	private final InteractionSequence completeSequence;

	public James(Location location) {
		super(ChatColor.GREEN + "James", 15, location, TEXTURE_DATA, TEXTURE_SIGNATURE);

		turnInSequence = new InteractionSequence(1, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					Quests.REPORTING_FOR_DUTY.getObjective(0).complete(pc);
				} else {
					SPEAK_NOISE.play(getLocation());
				}
			}
		};
		completeSequence = new InteractionSequence(1, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				SPEAK_NOISE.play(getLocation());
			}
		};

		Notice.createNotice(NoticeType.QUEST, location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.COMPLETED)) {
			completeSequence.advance(pc);
		} else {
			turnInSequence.advance(pc);
		}
	}

}
