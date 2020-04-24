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
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class FoodQuestGiver extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	private final InteractionSequence sequence1;
	private final InteractionSequence sequence2;

	public FoodQuestGiver(Location location) {
		super(ChatColor.GREEN + "Farmer", 7, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		Notice.createNotice(NoticeType.QUEST, location.clone().add(0, 2.25, 0));

		sequence1 = new InteractionSequence(3, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 2) {
					Quests.RECOVERING_THE_FOOD.start(pc);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};

		sequence2 = new InteractionSequence(3, 3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					Quests.RECOVERING_THE_FOOD.getObjective(0).complete(pc);
					pc.removeItem(Items.FOOD_SUPPLIES, 15);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			say("Please speak with " + ChatColor.GRAY + "[" + ChatColor.GREEN + "Combat Trainer" + ChatColor.GRAY + "]"
					+ ChatColor.WHITE + " before speaking with me.", pc);
		} else {
			if (Quests.RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.NOT_STARTED)) {
				sequence1.advance(pc);
			} else if (Quests.RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
				if (pc.getItemCount(Items.FOOD_SUPPLIES) >= 15) {
					sequence2.advance(pc);
				} else {
					say("Go get those food supplies!", pc);
				}
			} else if (Quests.RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.COMPLETED)) {
				if (Quests.GATHERING_MORE_FOOD.compareStatus(pc, QuestStatus.NOT_STARTED)) {
					Quests.GATHERING_MORE_FOOD.start(pc);
				} else if (Quests.GATHERING_MORE_FOOD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
					if (pc.getItemCount(Items.BOAR_FLANK) >= 10) {
						pc.removeItem(Items.BOAR_FLANK, 10);
						Quests.GATHERING_MORE_FOOD.getObjective(0).setProgress(pc, 1);
					} else {
						say("The people need to be fed. I'm counting on you.");
					}
				} else {
					say("Thanks to you, our children will have full bellies.", pc);
				}
			}
		}
	}

	// if (SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.NOT_STARTED)
	// && (REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.IN_PROGRESS)
	// || REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.COMPLETED))) {
	// sequence1.advance(pc);
	// } else if (SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
	// if (SLAYING_THE_THIEVES.getObjective(0).isComplete(pc)) {
	// sequence2.advance(pc);
	// } else {
	// say("Go get those bandits.", pc);
	// }
	// }

}
