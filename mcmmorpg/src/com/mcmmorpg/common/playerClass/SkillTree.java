package com.mcmmorpg.common.playerClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

public class SkillTree {

	private final PlayerClass playerClass;

	SkillTree(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public void open(PlayerCharacter pc) {
		Inventory inventory = createInventory(pc);
		pc.getPlayer().openInventory(inventory);
	}

	private Inventory createInventory(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		int size = 6 * 9;
		String title = playerClass.getName() + " Skill Tree";
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

}
