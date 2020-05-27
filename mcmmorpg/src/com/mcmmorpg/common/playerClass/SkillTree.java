package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * The center of player character advancement. A skill tree allows player
 * character to unlock and upgrade new skills by clicking in a skill tree menu.
 */
public class SkillTree {

	private final PlayerClass playerClass;

	final Map<PlayerCharacter, Inventory> inventoryMap;

	SkillTree(PlayerClass playerClass) {
		this.playerClass = playerClass;
		inventoryMap = new HashMap<>();
	}

	/**
	 * Opens the skill tree for ths specified player character.
	 */
	public void open(PlayerCharacter pc) {
		Inventory inventory = createInventory(pc);
		inventoryMap.put(pc, inventory);
		pc.getPlayer().openInventory(inventory);
	}

	private Inventory createInventory(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		int size = 6 * 9;
		int upgradePoints = pc.getSkillUpgradePoints();
		String title = playerClass.getName() + " Skill Tree (" + upgradePoints + " skill point"
				+ (upgradePoints == 1 ? "" : "s") + ")";
		Inventory inventory = Bukkit.createInventory(player, size, title);
		Skill[] skills = playerClass.getSkills();
		for (int i = 0; i < skills.length; i++) {
			Skill skill = skills[i];
			ItemStack itemStack = skill.getSkillTreeItemStack(pc);
			int slot = skill.getSkillTreeRow() * 9 + skill.getSkillTreeColumn();
			inventory.setItem(slot, itemStack);
		}
		return inventory;
	}

	Skill getSkillAt(int skillRow, int skillColumn) {
		Skill[] skills = playerClass.getSkills();
		for (int i = 0; i < skills.length; i++) {
			Skill skill = skills[i];
			if (skill.getSkillTreeRow() == skillRow && skill.getSkillTreeColumn() == skillColumn) {
				return skill;
			}
		}
		return null;
	}

}
