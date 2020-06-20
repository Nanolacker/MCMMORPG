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

public class GuardThomas extends StaticHuman {

	private static final int LEVEL = 25;
	private static final String TEXTURE_DATA = "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIsCiAgICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICAgInRleHR1cmVJZCIgOiAiNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MjE2ZmEyZWU1NmMzYmI2ZTUxZjVmNDNiYTM2MGIzZTNjMGQyOTkxZTlmZTk2Y2FhOGUxOGY4Yjc2NzJmYzdmIiwKICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICJ0ZXh0dXJlSWQiIDogIjUyMTZmYTJlZTU2YzNiYjZlNTFmNWY0M2JhMzYwYjNlM2MwZDI5OTFlOWZlOTZjYWE4ZTE4ZjhiNzY3MmZjN2YiCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=";
	private static final String TEXTURE_SIGNATURE = "EQ6XD3XObm5Z5zkTRPsFQXKeNiKngeQz19trIYkzYqfPPvXH6xOWyIkH9GT3tKqTNYNIs3kybg1ISu+rXkEgWznF1T9cHR8ylwtD/X99szXpbDWsOVI+WPVewgkflefsHxSwgGeIc7wXPcZF4xXuHppiJ06MOhzG8tHOfKGFmC22bwwo+kEY/+wxbYuZqCNvchzKwJZVoQFj9rxDoIaAz6BhQHfTo+dw/GnjU5E8FHk1LM61VU8mcjSiaSglPa8D1J18NSfYOz8VtkfgA87loJsxTsiupT8Ca7+ej2VsvYPETkH4kwOqo7Jr08cavwJhh1HY9OaVfhHFVM7SCpYbeYlDWfzgvRBbfENcrudUXFkJ3YuF/bhDtQK5PSYfltVanSglHYlF8iNnOa0iO5ar3/D0GuJZmjVmVpUbvlYQ9uawYTfSGIO+kjKj//qewW7EYp2x6C+qlN/JqmUw7j7Jz+IN5VfcORS0ByQnZPDI1xGI4jr3jbLRPlm6YesywDv3BceT6PAfNWygRl/Rf8MaWYdXWdz6gJsEyDZsPU6xNrOAVPxVM1I5lMdr77S9AU9mTADlK3Li+/cScEspKbpeEaKIeqbKNEcCZe9zKV3UCFcV1rYJfPBB+vagrC6xO1nHc1C9rJXP4vdNMIhoabvFIx4lM5cCadoGMhtuVIjkQO4=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final int DRIVING_OUT_THE_BANDITS_XP_REWARD = 300;

	private final InteractionSequence startDrivingOutTheBanditsInteraction;
	private final InteractionSequence completeDrivingOutTheBanditsInteraction;

	public GuardThomas(Location location) {
		super(ChatColor.GREEN + "Guard Thomas", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(this) {
			@Override
			protected QuestMarkerDisplayType getDisplayType(PlayerCharacter pc) {
				return QuestMarkerDisplayType.HIDDEN;
			}
		};
		startDrivingOutTheBanditsInteraction = new InteractionSequence(5) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Hey there, adventurer.", pc);
					break;
				case 1:
					say("Happy to know you're here to help us. You must be strong if the mayor send you.", pc);
					break;
				case 2:
					say("You're right on time, 'cause we got quite the case of bandits lurkin' about these sewers. These criminals will kill for money.",
							pc);
					break;
				case 3:
					say("Come back once their guild is no more. They like to hide in the rooms scattered throughout the sewers.",
							pc);
					break;
				case 4:
					Quests.DRIVING_OUT_THE_BANDITS.start(pc);
					break;
				}
			}
		};
		completeDrivingOutTheBanditsInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Thank you, you've been huge help.", pc);
					break;
				case 1:
					Quests.DRIVING_OUT_THE_BANDITS.getObjective(2).complete(pc);
					pc.giveXp(DRIVING_OUT_THE_BANDITS_XP_REWARD);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.DRIVING_OUT_THE_BANDITS.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startDrivingOutTheBanditsInteraction.advance(pc);
		} else if (Quests.DRIVING_OUT_THE_BANDITS.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.DRIVING_OUT_THE_BANDITS.getObjective(0).isComplete(pc)
					&& Quests.DRIVING_OUT_THE_BANDITS.getObjective(1).isComplete(pc)) {
				completeDrivingOutTheBanditsInteraction.advance(pc);
			} else {
				say("There's bandits afoot, adventurer.", pc);
			}
		} else {
			say("Hello there!", pc);
		}
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
