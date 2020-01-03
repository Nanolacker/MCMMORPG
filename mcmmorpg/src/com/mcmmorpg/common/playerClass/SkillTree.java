package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.utils.Debug;

public class SkillTree implements Listener {

	private final PlayerClass playerClass;
	private final Map<PlayerCharacter, Inventory> inventoryMap;

	SkillTree(PlayerClass playerClass) {
		this.playerClass = playerClass;
		inventoryMap = new HashMap<>();
		EventManager.registerEvents(this);
	}

	public void open(PlayerCharacter pc) {
		Inventory inventory = createInventory(pc);
		inventoryMap.put(pc, inventory);
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

	@EventHandler
	private void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Inventory inventory = event.getClickedInventory();
		if (!inventoryMap.get(pc).equals(inventory)) {
			return;
		}
		event.setCancelled(true);
		int slot = event.getSlot();
		int skillRow = slot / 9;
		int skillColumn = slot % 9;
		Skill skill = getSkillAt(skillRow, skillColumn);
		ClickType click = event.getClick();
		if (click.isShiftClick()) {
			// unlock/upgrade
			int skillPoints = 1; // pc.getSkillPoints();
			if (skill.isUnlocked(pc)) {
				if (skillPoints > 0) {
					skill.upgrade(pc);
				}else {
					pc.sendMessage("No skill points remaining");
				}
			}
		} else {
			// add to hotbar
		}
	}

	private Skill getSkillAt(int skillRow, int skillColumn) {
		Skill[] skills = playerClass.getSkills();
		for (int i = 0; i < skills.length; i++) {
			Skill skill = skills[i];
			if (skill.getSkillTreeRow() == skillRow && skill.getSkillTreeColumn() == skillColumn) {
				return skill;
			}
		}
		return null;
	}

	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		if (inventoryMap.containsKey(pc)) {
			inventoryMap.remove(pc);
		}
	}

}
