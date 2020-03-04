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
	private static final Quest MAGE_1 = Quest.forName("Tutorial Part 1 (Mage)");
	private static final Quest MAGE_2 = Quest.forName("Tutorial Part 2 (Mage)");
	private static final Quest MAGE_3 = Quest.forName("Tutorial Part 3 (Mage)");
	private static final Quest GOING_TO_THE_NEXT_NPC = null;

	private final MessageSequence fighter1Sequence;
	private MessageSequence fighter2Sequence;
	private MessageSequence fighter3Sequence;

	public TutorialGuide(Location location) {
		super(ChatColor.GREEN + "Tutorial Guide", 5, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		fighter1Sequence = new MessageSequence(4, formatDialgoue(new String[] { "Welcome newcomer!", null,
				"What brings you to Eladrador?", "Fame? Riches? Or perhaps serving the greater good?",
				"Very well. Allow me to put you on the path to success.", "The name's [NPC] by the way. And you?",
				"Oh.", "What a craptastic name.", "Moreover, let's get to work.",
				"I want you to show me what you've got by pitting you up against some of these merciless training dummies.",
				"Take out 5 of them and return to me.", "Good luck. They're merciless.", null })) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				SPEAK_NOISE.play(pc);
				if (messageIndex == 1) {
					FIGHTER_1.getObjective(0).setProgress(pc, 1);
				}
				if (messageIndex == 12) {
					FIGHTER_2.start(pc);
				}
			}
		};

		fighter2Sequence = new MessageSequence(4,
				formatDialgoue(
						new String[] { "You did it, rookie!", null, "Now I want you to do something cool.",
								"Tap 9 on your keyboard to open up the menu.",
								"From there, click on the Skill Tree icon to open up your skill tree.",
								"Here, you can unlock and upgrade powerful abilities to use in combat.",
								"Shift-click the icon at the top to unlock " + ChatColor.GREEN + "Bash" + ChatColor.GRAY
										+ ".",
								"Then, add it to your hotbar by left-clicking the icon.",
								"You can use skills by tapping the number key that corresponds to it on your hotbar.",
								"Go on now, try using it on some dummies.", null })) {
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == 1) {
					FIGHTER_2.getObjective(1).setProgress(pc, 1);
				} else if (messageIndex == 10) {
					FIGHTER_3.start(pc);
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
			} else if (FIGHTER_3.getStatus(pc) == QuestStatus.NOT_STARTED) {
				if (FIGHTER_2.getObjective(0).isComplete(pc) || FIGHTER_2.getStatus(pc) == QuestStatus.COMPLETED) {
					fighter2Sequence.advance(pc);
				} else {
					say("Go on champ. Show those dummies who's boss.", pc);
				}
			} else if (GOING_TO_THE_NEXT_NPC.getStatus(pc) == QuestStatus.NOT_STARTED) {
				if (FIGHTER_3.getObjective(0).isComplete(pc) && FIGHTER_3.getObjective(1).isComplete(pc)) {
					FIGHTER_3.getObjective(2).setProgress(pc, 1);
				}
			} else {
				say("Good luck out there champ.", pc);
			}
		}
	}

}
