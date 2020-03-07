package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.MessageSequence;
import com.mcmmorpg.common.ui.QuestNotice;

import net.md_5.bungee.api.ChatColor;

public class BanditQuestGiver extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	private static final Quest REPORTING_FOR_DUTY = Quest.forName("Reporting for Duty");
	private static final Quest SLAYING_THE_THIEVES = Quest.forName("Slaying the Thieves");

	private final MessageSequence sequence1;
	private final MessageSequence sequence2;

	public BanditQuestGiver(Location location) {
		super(ChatColor.GREEN + "Bandit Quest Giver", 7, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestNotice.createQuestNotice(location.clone().add(0, 2.25, 0));

		sequence1 = new MessageSequence(3, this, "Greetings adventurer.", null, "Go kill some thieves.", null) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					REPORTING_FOR_DUTY.getObjective(0).setProgress(pc, 1);
				} else if (messageIndex == 3) {
					SLAYING_THE_THIEVES.start(pc);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};

		sequence2 = new MessageSequence(3, this, "Thank you for your help.", null) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					SLAYING_THE_THIEVES.getObjective(1).complete(pc);
				} else {
					SPEAK_NOISE.play(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			say("You should complete your training, adventurer.", pc);
		} else if (SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.NOT_STARTED)
				&& (REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.IN_PROGRESS)
						|| REPORTING_FOR_DUTY.compareStatus(pc, QuestStatus.COMPLETED))) {
			sequence1.advance(pc);
		} else if (SLAYING_THE_THIEVES.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (SLAYING_THE_THIEVES.getObjective(0).isComplete(pc)) {
				sequence2.advance(pc);
			} else {
				say("Go get those bandits.", pc);
			}
		}
	}

}
