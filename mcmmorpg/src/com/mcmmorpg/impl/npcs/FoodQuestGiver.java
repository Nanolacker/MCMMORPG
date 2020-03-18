package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.MessageSequence;
import com.mcmmorpg.common.ui.Notice;
import com.mcmmorpg.common.ui.Notice.NoticeType;
import com.mcmmorpg.impl.ItemManager;

public class FoodQuestGiver extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	private static final Quest SLAYING_THE_THIEVES = Quest.forName("Slaying the Thieves");
	private static final Quest RECOVERING_THE_FOOD = Quest.forName("Recovering the Food");
	private static final Quest GATHERING_MORE_FOOD = Quest.forName("Gathering More Food");

	private final MessageSequence sequence1;
	private final MessageSequence sequence2;

	public FoodQuestGiver(Location location) {
		super(ChatColor.GREEN + "Farmer", 7, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		Notice.createNotice(NoticeType.QUEST, location.clone().add(0, 2.25, 0));

		sequence1 = new MessageSequence(3, this, "Hello there.", "Recover some food supplies.", null) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 2) {
					RECOVERING_THE_FOOD.start(pc);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};

		sequence2 = new MessageSequence(3, this, "You recovered the food!.", null) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					RECOVERING_THE_FOOD.getObjective(0).complete(pc);
					pc.removeItem(ItemManager.FOOD_SUPPLIES, 15);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			say("Please speak with " + ChatColor.GRAY + "[" + ChatColor.GREEN + "Combat Trainer" + ChatColor.GRAY + "]"
					+ ChatColor.WHITE + " before speaking with me.", pc);
		} else {
			if (RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.NOT_STARTED)) {
				sequence1.advance(pc);
			} else if (RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
				if (pc.getItemCount(ItemManager.FOOD_SUPPLIES) >= 15) {
					sequence2.advance(pc);
				} else {
					say("Go get those food supplies!", pc);
				}
			} else if (RECOVERING_THE_FOOD.compareStatus(pc, QuestStatus.COMPLETED)) {
				if (GATHERING_MORE_FOOD.compareStatus(pc, QuestStatus.NOT_STARTED)) {
					GATHERING_MORE_FOOD.start(pc);
				} else if (GATHERING_MORE_FOOD.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
					if (pc.getItemCount(ItemManager.BOAR_FLANK) >= 10) {
						pc.removeItem(ItemManager.BOAR_FLANK, 10);
						GATHERING_MORE_FOOD.getObjective(0).setProgress(pc, 1);
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
