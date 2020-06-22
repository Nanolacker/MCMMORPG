package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.CharacterKillEvent;
import com.mcmmorpg.common.event.PlayerCharacterRegisterEvent;
import com.mcmmorpg.common.item.Item;

class QuestObjectiveListener implements Listener {

	private static final Map<Item, QuestObjective> ITEM_COLLECTION_OBJECTIVES = new HashMap<>();
	private static final Map<Class<? extends AbstractCharacter>, QuestObjective> SLAY_CHARACTER_OBJECTIVES = new HashMap<>();

	static void registerItemCollectionObjective(Item item, QuestObjective objective) {
		ITEM_COLLECTION_OBJECTIVES.put(item, objective);
	}

	static void registerSlayCharacterObjective(Class<? extends AbstractCharacter> characterType,
			QuestObjective objective) {
		SLAY_CHARACTER_OBJECTIVES.put(characterType, objective);
	}

	@EventHandler
	private void onRegisterPc(PlayerCharacterRegisterEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
	}

	@EventHandler
	private void onKillCharacter(CharacterKillEvent event) {
		Source source = event.getKiller();
		if (source instanceof PlayerCharacter) {
			PlayerCharacter pc = (PlayerCharacter) source;
			AbstractCharacter killed = event.getKilled();
			Class<? extends AbstractCharacter> characterType = killed.getClass();
			QuestObjective objective = SLAY_CHARACTER_OBJECTIVES.get(characterType);
			if (objective != null) {
				if (objective.isAccessible(pc)) {
					objective.addProgress(pc, 1);
				}
			}
		}
	}

}
