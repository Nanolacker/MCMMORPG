package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.PlayerClasses;
import com.mcmmorpg.impl.constants.Quests;

public class GuardJames extends StaticHuman {

	private static final int LEVEL = 25;
	private static final String TEXTURE_DATA = "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIsCiAgICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICAgInRleHR1cmVJZCIgOiAiNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MjE2ZmEyZWU1NmMzYmI2ZTUxZjVmNDNiYTM2MGIzZTNjMGQyOTkxZTlmZTk2Y2FhOGUxOGY4Yjc2NzJmYzdmIiwKICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICJ0ZXh0dXJlSWQiIDogIjUyMTZmYTJlZTU2YzNiYjZlNTFmNWY0M2JhMzYwYjNlM2MwZDI5OTFlOWZlOTZjYWE4ZTE4ZjhiNzY3MmZjN2YiCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=";
	private static final String TEXTURE_SIGNATURE = "EQ6XD3XObm5Z5zkTRPsFQXKeNiKngeQz19trIYkzYqfPPvXH6xOWyIkH9GT3tKqTNYNIs3kybg1ISu+rXkEgWznF1T9cHR8ylwtD/X99szXpbDWsOVI+WPVewgkflefsHxSwgGeIc7wXPcZF4xXuHppiJ06MOhzG8tHOfKGFmC22bwwo+kEY/+wxbYuZqCNvchzKwJZVoQFj9rxDoIaAz6BhQHfTo+dw/GnjU5E8FHk1LM61VU8mcjSiaSglPa8D1J18NSfYOz8VtkfgA87loJsxTsiupT8Ca7+ej2VsvYPETkH4kwOqo7Jr08cavwJhh1HY9OaVfhHFVM7SCpYbeYlDWfzgvRBbfENcrudUXFkJ3YuF/bhDtQK5PSYfltVanSglHYlF8iNnOa0iO5ar3/D0GuJZmjVmVpUbvlYQ9uawYTfSGIO+kjKj//qewW7EYp2x6C+qlN/JqmUw7j7Jz+IN5VfcORS0ByQnZPDI1xGI4jr3jbLRPlm6YesywDv3BceT6PAfNWygRl/Rf8MaWYdXWdz6gJsEyDZsPU6xNrOAVPxVM1I5lMdr77S9AU9mTADlK3Li+/cScEspKbpeEaKIeqbKNEcCZe9zKV3UCFcV1rYJfPBB+vagrC6xO1nHc1C9rJXP4vdNMIhoabvFIx4lM5cCadoGMhtuVIjkQO4=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final int ARACHNOPHOBIA_XP_REWARD = 150;

	private final InteractionSequence startArachnophobiaInteraction;
	private final InteractionSequence completeArachnophobiaInteraction;

	public GuardJames(Location location) {
		super(ChatColor.GREEN + "Guard James", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(location.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerDisplayType getDisplayType(PlayerCharacter pc) {
				return QuestMarkerDisplayType.HIDDEN;
			}
		};
		startArachnophobiaInteraction = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("Greetings, traveller.", pc);
					break;
				case 1:
					speak("I am one of Melcher's finest guardsmen. I protect the town from this post.", pc);
					break;
				case 2:
					speak("Recently we've received reports of giant spiders along the road. Despicable, wretched things.",
							pc);
					break;
				case 3:
					speak("Those beady eyes... Their spindly, horrifying legs... Enough to make a grown man squeal in fright.",
							pc);
					break;
				case 4:
					speak("Ehem. You have a weapon. Why don't you take care of them?", pc);
					break;
				case 5:
					speak("I'll stay here where it's safe-er, to fend off any highwaymen. Yes, highwaymen. Not spiders.",
							pc);
					break;
				case 6:
					Quests.ARACHNOPHOBIA.start(pc);
					break;
				}
			}
		};
		completeArachnophobiaInteraction = new InteractionSequence(6) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					speak("You did it? The brood mother as well?", pc);
					break;
				case 1:
					speak("Thank you for your help, traveller. You've truly made my day.", pc);
					break;
				case 2:
					speak("Dear gods, thank you so much. Now I can finally sleep without a candle lit.", pc);
					break;
				case 3:
					speak("Uh, rather, now the trade routes are safer because of you.", pc);
					break;
				case 4:
					speak("Here, a little something from our barracks. A weapon fit for a hero such as yourself.", pc);
					break;
				case 5:
					Quests.ARACHNOPHOBIA.getObjective(2).complete(pc);
					pc.giveXp(ARACHNOPHOBIA_XP_REWARD);
					PlayerClass playerClass = pc.getPlayerClass();
					if (playerClass == PlayerClasses.FIGHER) {
						pc.giveItem(Items.SPEAR_OF_THE_MELCHER_GUARD);
					} else if (playerClass == PlayerClasses.MAGE) {
						pc.giveItem(Items.STAFF_OF_THE_MELCHER_GUARD);
					}
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.ARACHNOPHOBIA.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startArachnophobiaInteraction.advance(pc);
		} else if (Quests.ARACHNOPHOBIA.compareStatus(pc, QuestStatus.COMPLETED)) {
			speak("I owe you one.", pc);
		} else if (Quests.ARACHNOPHOBIA.getObjective(0).isComplete(pc)
				&& Quests.ARACHNOPHOBIA.getObjective(1).isComplete(pc)) {
			completeArachnophobiaInteraction.advance(pc);
		} else {
			speak("Go on now. Rid the lands of those monsters.", pc);
		}
	}

	@Override
	public void speak(String dialogue, PlayerCharacter recipient) {
		super.speak(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
