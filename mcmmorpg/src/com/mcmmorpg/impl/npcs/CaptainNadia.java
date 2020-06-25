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

public class CaptainNadia extends StaticHuman {

	private static final int LEVEL = 25;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDUzMDcyMTIyOCwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyODRkMTdmYWQ5NDhkMDhmNDU3OTNiOGY2YWE5YzcwNWFlZTQ0YTBlOTYxZWM4Zjg5MDE2NDY5MjczOTE3YSIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "DHPMPxP88LtjQEOkNbemcVnOWd/piuGjpB9lKnr1KwsT+S+d9+UPeISSgZFVunVduwbmT2NnU+AuUfiBxm4MwT8j9wad/9ZtBT/JKII1DTzLyaFW7K0nSwefpuiK2VMjyQ1AXszHS8UuFVhTNd9QfRmSJpaVldh0l/o00xpI8C8N4A/eMU5P6mtQm3DSisZY3AfvpvfXBzE6tvBR4ivZ2wGSQtTBK8tbowWs1ahf8hsAINE6FAHM/BwTVnqCKBUYcBXFOaq/6EI0IMHPwXPa2prOiQxDWaQsPb4WE4WKuT1qxrFfixCe2i5B4h3QuLYVNcjgnj3UFpbcO1disBcmOQP97EcelzOShzWUs3gDgnWpKZc5VBI3fexSKI7fBcUz64RBed45k9dN2f/BFbEhrbEdn//UqHHr/eT3kD24+/n2N6ZHjfGJ9ONY0e/UFW+GGbbhjZ22HDyNe8NEstAuWZvBwqeYCqzd8wal53zqsqhq75o8Shyc9GQ2eCNV/PFmQksk7NUkIrjU3bj3lL+fpWfuFQYCYatOndQ8oOHbTlZMTcRqNp/hUNVMkDmZGz0azuFTUQsU3F0ly5IwThvXPwGvWbSG1ysxfx4ig9UnDQeF908EFEq8eNJWUtdXTGX7ZSrrxE6tvA1oTWZWh6UJXI1t/T++W7mUXfPb6a2AHQw=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);
	private static final int INTO_THE_SEWERS_XP_REWARD = 100;
	private static final int CULLING_THE_CULT_XP_REWARD = 400;

	private final InteractionSequence completeIntoTheSewersInteraction;
	private final InteractionSequence completeCullingTheCultInteraction;

	public CaptainNadia(Location location) {
		super(ChatColor.GREEN + "Captain Nadia", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(Quests.INTO_THE_SEWERS, location.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				return QuestMarkerIcon.HIDDEN;
			}
		};
		completeIntoTheSewersInteraction = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("The mayor sent you, eh? Let's hope you're as good as he thinks.", pc);
					break;
				case 1:
					speak("If you haven't noticed already, this place is chalk full of thick, magic sludge that dissolves anything it touches.",
							pc);
					break;
				case 2:
					speak("I guess an unpredictable, filthy concoction is what happens when alchemical leftovers and human waste mix together.",
							pc);
					break;
				case 3:
					speak("Anyway, seems that cult activity has involved summoning undead and sacrifices.", pc);
					break;
				case 4:
					speak("The bandits must be getting payed a hefty sum to be helping these freaks.", pc);
					break;
				case 5:
					speak("I need you to venture out and take care of the cultists. They've been deemed too dangerous to simply arrest.",
							pc);
					break;
				case 6:
					Quests.INTO_THE_SEWERS.getObjective(1).complete(pc);
					pc.giveXp(INTO_THE_SEWERS_XP_REWARD);
					Quests.CULLING_THE_CULT.start(pc);
					Quests.CULLING_THE_CULT.getObjective(0).setAccessible(pc, true);
					Quests.CULLING_THE_CULT.getObjective(1).setAccessible(pc, true);
					break;
				}
			}
		};
		completeCullingTheCultInteraction = new InteractionSequence(6) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("Good to see you made it back alive. Those gashes will make for some impressive scars.", pc);
					break;
				case 1:
					speak("... A god of death wannabe? That's what those fanatics were worshipping?", pc);
					break;
				case 2:
					speak("This is way worse than what we were expecting... This Xylphanos character sounds like a piece of work.",
							pc);
					break;
				case 3:
					speak("Still, with that sort of power he is a serious threat. We must alert all corners of Eladrador to be especially vigilant.",
							pc);
					break;
				case 4:
					speak("Report these findings back to mayor as soon as possible. My squadron will stay down here to scout out any remaining bandits.",
							pc);
					break;
				case 5:
					Quests.CULLING_THE_CULT.getObjective(2).complete(pc);
					pc.giveXp(CULLING_THE_CULT_XP_REWARD);
					Quests.THREAT_LEVEL_GOD.start(pc);
					Quests.THREAT_LEVEL_GOD.getObjective(0).setAccessible(pc, true);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.INTO_THE_SEWERS.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			completeIntoTheSewersInteraction.advance(pc);
		} else if (Quests.CULLING_THE_CULT.compareStatus(pc, QuestStatus.COMPLETED)) {
			speak("I owe you one.", pc);
		} else if (Quests.CULLING_THE_CULT.getObjective(0).isComplete(pc)
				&& Quests.CULLING_THE_CULT.getObjective(1).isComplete(pc)) {
			completeCullingTheCultInteraction.advance(pc);
		} else {
			speak("Go on now.", pc);
		}
	}

	@Override
	public void speak(String dialogue, PlayerCharacter recipient) {
		super.speak(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}
}
