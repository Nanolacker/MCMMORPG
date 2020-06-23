package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Quests;

public class FlintonMasterAlchemist extends StaticHuman {

	private static final int LEVEL = 20;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MTgyMDE1NzY3NSwKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMTBhMmFmN2IzOTk0MDZjNTRiNzE1NGRkNzBjZjljOTljZDgxYjg4MTA5YTg0ZGRjN2YwMGY3MzIwMTdkMmYzIgogICAgfQogIH0KfQ==";
	private static final String TEXTURE_SIGNATURE = "puGFxU8lQZpJnSE8SK5A1pwHvmvZdiDYAmdYpxZk6mc3larVX3PjqGWH+Of8uHYyHKRPfJ4bef93QR1Hy1yEhHD3Qtr9tnUEG3DbxLUbIksg+Qr8Ra82EM0KCXqs2MLy8aCbbXk55kz5JpEqEgfkMqvGJZuGAtwyqAzTkdGWhDEN8Fopw30IAG6CyQ/5BuFZuascY9U9Wtboi6hnxE5kU3lyhDO3o4Sx6nH0FnZhsiLGxX5Z7erGNz9rclsBmOQYc30WMX8AcHYCSQIOzIxCT2cFxce8IppLy9gQ8bwZYBjIJqrp4RU6gpC1hxobaSJaJ4XQTGfH152mLwsWGC/MITp8QGktFo2Z3S7DLZ++RI18JMg23UY+Ah98+JiicpWJDfA/MTjc8iT4Mxj6EUGZxO8CCwM4ZzidsW+BHTgN+z+6DPbom2nbGk1lb5qiKV++byBWux0d7RbZtTfZ7e+mEMNvmGjSuuqYmTg20AZ1HNt8P1Ga+KkXnlgho5qgADiDmf+M4TkkriiKRlz1zfYR7ROAPhvbaZk2/X8cIfCXIK4o/Jpl5vRkbjidB8NgSAm50WK3GzMSvUEjQHI6A1r1vMkWSBaIMgJ+V1rMpLS/m0M9fBunxt8BG9uGFk0aoY/udNoTbLQgeuDaWI/lhOOWEOgzqIIHXPdS/CPaiH78bXc=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);
	private static final int BOARS_GALORE_XP_REWARD = 750;

	private final InteractionSequence startBoarsGaloreInteraction;
	private final InteractionSequence completeBoarsGaloreInteraction;

	public FlintonMasterAlchemist(Location location) {
		super(ChatColor.GREEN + "Master Alchemist", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(Quests.BOARS_GALORE, location.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				return QuestMarkerIcon.HIDDEN;
			}
		};
		startBoarsGaloreInteraction = new InteractionSequence(5) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					speak("Greetings! Lovely day to tend to some stewing cauldrons, wouldn't you say?", pc);
					break;
				case 1:
					speak("Well, that's what me and my fellow alchemists do here all day. Welcome to the Potion Factory!",
							pc);
					break;
				case 2:
					speak("I'd offer you health potions for sale, but unfortunately bandits have been slowing down our ingredient deliveries. We're low on stock.",
							pc);
					break;
				case 3:
					speak("Actually, would you be able to gather boar tusks for us? The powder is what we use to base our potions.",
							pc);
					break;
				case 4:
					Quests.BOARS_GALORE.start(pc);
					break;
				}
			}
		};
		completeBoarsGaloreInteraction = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					speak("Wonderful! Now we have materials until the supply lines are reestablished.", pc);
					break;
				case 1:
					speak("Have some health potions as thanks.", pc);
					break;
				case 2:
					pc.removeItem(Items.BOAR_TUSK, 15);
					Quests.BOARS_GALORE.getObjective(1).complete(pc);
					pc.giveXp(BOARS_GALORE_XP_REWARD);
					pc.giveItem(Items.POTION_OF_LESSER_HEALING, 3);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.BOARS_GALORE.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startBoarsGaloreInteraction.advance(pc);
		} else if (Quests.BOARS_GALORE.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.BOARS_GALORE.getObjective(0).isComplete(pc) && pc.getItemCount(Items.BOAR_TUSK) >= 15) {
				completeBoarsGaloreInteraction.advance(pc);
			} else {
				speak("Off you go now!", pc);
			}
		} else {
			speak("Welcome to the Potion Factory. Keep your arms and legs outside of the cauldrons, please.", pc);
		}
	}

	@Override
	public void speak(String dialogue, PlayerCharacter recipient) {
		super.speak(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
