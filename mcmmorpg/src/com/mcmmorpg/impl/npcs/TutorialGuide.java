package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.MessageSequence;

public class TutorialGuide extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	private static final Quest FIGHTER_1 = Quest.forName("Tutorial Part 1 (Fighter)");
	private static final Quest FIGHTER_2 = Quest.forName("Tutorial Part 2 (Fighter)");
	private static final Quest FIGHTER_3 = Quest.forName("Tutorial Part 3 (Fighter)");
	private static final Quest MAGE_1 = Quest.forName("Tutorial Part 1 (Mage)");;
	private static final Quest MAGE_2 = Quest.forName("Tutorial Part 2 (Mage)");;
	private static final Quest MAGE_3 = Quest.forName("Tutorial Part 3 (Mage)");;

	private final MessageSequence fighter1Sequence;
	private MessageSequence fighter2Sequence;
	private MessageSequence fighter3Sequence;

	public TutorialGuide(Location location) {
		super(ChatColor.GREEN + "Tutorial Guide", 5, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		fighter1Sequence = new MessageSequence(2, formatDialgoue(new String[] { "Welcome newcomer!",
				"What brings you to Eladrador?", "Fame? Riches? Or perhaps serving the greater good?",
				"Very well. Allow me to put you on the path to success.", "The name's [NPC] by the way. And you?",
				"Oh.", "What a craptastic name.", "Moreover, let's get to work.",
				"I want you to show me what you've got by pitting you up against some of these merciless training dummies.",
				"Take out 5 of them and return to me.", "Good luck. They're merciless." })) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				SPEAK_NOISE.play(pc);
				if (messageIndex == 0) {
					FIGHTER_1.getObjective(0).setProgress(pc, 1);
				}
				if (messageIndex == 10) {
					FIGHTER_2.start(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		PlayerClass playerClass = pc.getPlayerClass();
		if (playerClass.getName().equals("Fighter")) {
			if (FIGHTER_2.getStatus(pc) == QuestStatus.NOT_STARTED) {
				fighter1Sequence.advance(pc);
			} else if (FIGHTER_2.getStatus(pc) == QuestStatus.IN_PROGRESS) {
				if (FIGHTER_2.getObjective(0).getProgress(pc) == 5) {
					say("Good job newcomer.", pc);
					new DelayedTask(1) {
						@Override
						protected void run() {
							FIGHTER_2.getObjective(1).setProgress(pc, 1);
						}
					}.schedule();
				} else {
					say("Go on champ. Show those dummies who's boss.", pc);
				}
			}
		}

	}

}
