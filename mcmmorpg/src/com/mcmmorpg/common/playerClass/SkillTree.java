package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import com.mcmmorpg.common.sound.Noise;

import net.md_5.bungee.api.ChatColor;

public class SkillTree implements Listener {

	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

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

	@EventHandler
	private void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Inventory mappedInventory = inventoryMap.get(pc);
		if (mappedInventory == null) {
			return;
		}
		Inventory inventory = event.getClickedInventory();
		if (mappedInventory != inventory) {
			// when player clicks bottom inventory while skill tree open
			event.setCancelled(true);
			return;
		}
		event.setCancelled(true);
		int slot = event.getSlot();
		int skillRow = slot / 9;
		int skillColumn = slot % 9;
		Skill skill = getSkillAt(skillRow, skillColumn);
		if (skill == null) {
			return;
		}
		ClickType click = event.getClick();

		if (!skill.prerequisitesAreMet(pc)) {
			pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is not available");
			CLICK_NOISE.play(player);
			return;
		}

		if (click.isShiftClick()) {
			// unlock/upgrade
			int availableSillPoints = pc.getSkillUpgradePoints();
			if (availableSillPoints <= 0) {
				pc.sendMessage(ChatColor.GRAY + "No skill points remaining");
			} else if (skill.getUpgradeLevel(pc) == skill.getMaximumUpgradeLevel()) {
				pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is already at maximum level");
			} else {
				skill.upgrade(pc);
				pc.setSkillUpgradePoints(pc.getSkillUpgradePoints() - 1);
				// update skill tree inventory by reopening it
				this.open(pc);
			}

		} else {
			// add to inventory
			if (!skill.isUnlocked(pc)) {
				pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is not unlocked");
			} else {
				ItemStack skillItemStack = skill.getHotbarItemStack();
				Inventory playerInventory = player.getInventory();
				if (playerInventory.contains(skillItemStack)) {
					pc.sendMessage(
							ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is already on  your hotbar");
				} else {
					boolean noRoom = true;
					for (int i = 1; i < 9; i++) {
						ItemStack itemStack = playerInventory.getItem(i);
						if (itemStack == null) {
							playerInventory.setItem(i, skillItemStack);
							pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " added to hotbar");
							noRoom = false;
							break;
						}
					}
					if (noRoom) {
						pc.sendMessage(ChatColor.GRAY + "No room on hotbar");
					}
				}
			}
		}
		CLICK_NOISE.play(player);
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
		if (inventoryMap.get(pc) == event.getInventory()) {
			inventoryMap.remove(pc);
		}
	}

}
