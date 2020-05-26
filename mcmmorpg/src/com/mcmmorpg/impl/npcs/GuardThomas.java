package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Quests;

public class GuardThomas extends StaticHuman {

	private static final int LEVEL = 25;
	private static final String TEXTURE_DATA = "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIsCiAgICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICAgInRleHR1cmVJZCIgOiAiNTIxNmZhMmVlNTZjM2JiNmU1MWY1ZjQzYmEzNjBiM2UzYzBkMjk5MWU5ZmU5NmNhYThlMThmOGI3NjcyZmM3ZiIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiNjNlODY3MzY0YTNmNDAzMmI2M2E0YTU2NDE5ZTJjOWEiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MjE2ZmEyZWU1NmMzYmI2ZTUxZjVmNDNiYTM2MGIzZTNjMGQyOTkxZTlmZTk2Y2FhOGUxOGY4Yjc2NzJmYzdmIiwKICAgICJwcm9maWxlSWQiIDogImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwKICAgICJ0ZXh0dXJlSWQiIDogIjUyMTZmYTJlZTU2YzNiYjZlNTFmNWY0M2JhMzYwYjNlM2MwZDI5OTFlOWZlOTZjYWE4ZTE4ZjhiNzY3MmZjN2YiCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=";
	private static final String TEXTURE_SIGNATURE = "EQ6XD3XObm5Z5zkTRPsFQXKeNiKngeQz19trIYkzYqfPPvXH6xOWyIkH9GT3tKqTNYNIs3kybg1ISu+rXkEgWznF1T9cHR8ylwtD/X99szXpbDWsOVI+WPVewgkflefsHxSwgGeIc7wXPcZF4xXuHppiJ06MOhzG8tHOfKGFmC22bwwo+kEY/+wxbYuZqCNvchzKwJZVoQFj9rxDoIaAz6BhQHfTo+dw/GnjU5E8FHk1LM61VU8mcjSiaSglPa8D1J18NSfYOz8VtkfgA87loJsxTsiupT8Ca7+ej2VsvYPETkH4kwOqo7Jr08cavwJhh1HY9OaVfhHFVM7SCpYbeYlDWfzgvRBbfENcrudUXFkJ3YuF/bhDtQK5PSYfltVanSglHYlF8iNnOa0iO5ar3/D0GuJZmjVmVpUbvlYQ9uawYTfSGIO+kjKj//qewW7EYp2x6C+qlN/JqmUw7j7Jz+IN5VfcORS0ByQnZPDI1xGI4jr3jbLRPlm6YesywDv3BceT6PAfNWygRl/Rf8MaWYdXWdz6gJsEyDZsPU6xNrOAVPxVM1I5lMdr77S9AU9mTADlK3Li+/cScEspKbpeEaKIeqbKNEcCZe9zKV3UCFcV1rYJfPBB+vagrC6xO1nHc1C9rJXP4vdNMIhoabvFIx4lM5cCadoGMhtuVIjkQO4=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence startCleaningTheSewersInteraction;
	private final InteractionSequence completeCleaningTheSewersInteraction;

	public GuardThomas(Location location) {
		super(ChatColor.GREEN + "Guard Thomas", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
		startCleaningTheSewersInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Insert Dialogue", pc);
					break;
				case 1:
					Quests.CLEANING_THE_SEWERS.start(pc);
					break;
				}
			}
		};
		completeCleaningTheSewersInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				switch (messageIndex) {
				case 0:
					say("Insert dialogue", pc);
					break;
				case 1:
					Quests.CLEANING_THE_SEWERS.getObjective(4).complete(pc);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.CLEANING_THE_SEWERS.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startCleaningTheSewersInteraction.advance(pc);
		} else if (Quests.CLEANING_THE_SEWERS.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			QuestObjective[] objectives = Quests.CLEANING_THE_SEWERS.getObjectives();
			boolean readyToTurnIn = true;
			for (int i = 0; i < objectives.length - 1; i++) {
				if (!objectives[i].isComplete(pc)) {
					readyToTurnIn = false;
					break;
				}
			}
			if (readyToTurnIn) {
				completeCleaningTheSewersInteraction.advance(pc);
			} else {
				say("Go clear the sewers.", pc);
			}
		} else {
			say("What is it?", pc);
		}
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

}
