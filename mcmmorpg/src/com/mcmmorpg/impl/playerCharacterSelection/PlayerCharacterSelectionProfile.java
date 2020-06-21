package com.mcmmorpg.impl.playerCharacterSelection;

import java.io.File;

import org.bukkit.entity.Player;

import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Stores data regarding player character selection for an individual player.
 */
class PlayerCharacterSelectionProfile {

	private final Player player;
	private Menu openMenu;
	private int currentCharacterSlot;
	private PersistentPlayerCharacterDataContainer[] characterData;

	PlayerCharacterSelectionProfile(Player player) {
		this.player = player;
		openMenu = Menu.SELECT_CHARACTER;
		currentCharacterSlot = 0;
		characterData = new PersistentPlayerCharacterDataContainer[4];
		for (int i = 1; i <= 4; i++) {
			updateCharacterData(i);
		}
	}

	/**
	 * Reloads the character data of that slot from the file system.
	 */
	void updateCharacterData(int characterSlot) {
		characterData[characterSlot - 1] = fetchCharacterDataFromFile(characterSlot);
	}

	private PersistentPlayerCharacterDataContainer fetchCharacterDataFromFile(int characterSlot) {
		File characterSaveFile = PlayerCharacterSelectionListener.getCharacterSaveFile(player, characterSlot);
		if (characterSaveFile.exists()) {
			return IOUtility.readJsonFile(characterSaveFile, PersistentPlayerCharacterDataContainer.class);
		} else {
			return null;
		}
	}

	Menu getOpenMenu() {
		return openMenu;
	}

	void setOpenMenu(Menu menu) {
		this.openMenu = menu;
	}

	/**
	 * Returns the character slot being edited, either by creating or deleting a
	 * character of that slot.
	 */
	int getCurrentCharacterSlot() {
		return currentCharacterSlot;
	}

	public void setCurrentCharacterSlot(int characterSlot) {
		this.currentCharacterSlot = characterSlot;
	}

	PersistentPlayerCharacterDataContainer getCharacterData(int characterSlot) {
		return characterData[characterSlot - 1];
	}

	static enum Menu {
		SELECT_CHARACTER, SELECT_PLAYER_CLASS, DELETING_CHARACTER, CONFIRM_DELETION
	}

}
