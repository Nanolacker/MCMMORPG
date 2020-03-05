package com.mcmmorpg.common.playerClass;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

class PlayerClassListener implements Listener {

	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

	PlayerClassListener() {
		new RepeatingTask(0.1) {
			@Override
			protected void run() {
				List<PlayerCharacter> pcs = PlayerCharacter.getAll();
				for (int i = 0; i < pcs.size(); i++) {
					PlayerCharacter pc = pcs.get(i);
					PlayerClass playerClass = pc.getPlayerClass();
					Inventory inventory = pc.getPlayer().getInventory();
					for (int j = 1; j < 9; j++) {
						ItemStack itemStack = inventory.getItem(j);
						Skill skill = playerClass.skillForHotbarItemStack(itemStack);
						if (skill != null) {
							if (pc.isSilenced() || skill.isOnCooldown(pc)
									|| pc.getCurrentMana() < skill.getManaCost()) {
								itemStack.setType(Skill.DISABLED_MATERIAL);
								int cooldown = (int) Math.ceil(skill.getCooldown(pc));
								if (cooldown != 0) {
									itemStack.setAmount(cooldown);
								}
							} else {
								itemStack.setType(skill.getIcon());
							}
						}
					}
				}
			}
		}.schedule();
	}

	@EventHandler
	private void onChangeHeldItem(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}

		Inventory inventory = player.getInventory();
		int slot = event.getNewSlot();
		ItemStack itemStack = inventory.getItem(slot);
		if (itemStack == null) {
			return;
		}

		PlayerClass playerClass = pc.getPlayerClass();
		Skill skill = playerClass.skillForHotbarItemStack(itemStack);
		if (skill == null) {
			return;
		}

		if (pc.isSilenced()) {
			CLICK_NOISE.play(player);
		} else if (skill.isOnCooldown(pc)) {
			pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is on cooldown (" + ChatColor.YELLOW
					+ (int) Math.ceil(skill.getCooldown(pc)) + ChatColor.GRAY + ")");
			CLICK_NOISE.play(player);
		} else if (pc.getCurrentMana() < skill.getManaCost()) {
			pc.sendMessage(ChatColor.GRAY + "Not enough " + ChatColor.AQUA + "MP " + ChatColor.GRAY + "to use "
					+ ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " (" + ChatColor.AQUA
					+ (int) Math.ceil(skill.getManaCost()) + ChatColor.GRAY + ")");
			CLICK_NOISE.play(player);
		} else {
			skill.use(pc);
		}
	}

	@EventHandler
	private void onClickSkillHotbarItemStack(InventoryClickEvent event) {
		if (event.isShiftClick() || event.getAction() == InventoryAction.HOTBAR_SWAP) {
			event.setCancelled(true);
			return;
		}
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}

		ItemStack clickedItemStack = event.getCurrentItem();
		ItemStack droppedItemStack = event.getCursor();
		PlayerClass playerClass = pc.getPlayerClass();
		Skill clickedSkill = playerClass.skillForHotbarItemStack(clickedItemStack);
		if (clickedSkill != null) {
			clickedItemStack.setAmount(1);
			clickedItemStack.setType(clickedSkill.getIcon());
		}
		Skill droppedSkill = playerClass.skillForHotbarItemStack(droppedItemStack);
		if (droppedSkill != null) {
			int slot = event.getRawSlot();
			if (slot == -999) {
				player.sendMessage(ChatColor.GREEN + droppedSkill.getName() + ChatColor.GRAY + " removed from hotbar");
			} else if (slot == 36) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.GRAY + "The first slot of your hotbar is reserved for your weapon");
			} else if (slot < 37 || slot > 44) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.GRAY + "Skills can only be placed on your hotbar");
			}
		}
	}

	@EventHandler
	private void onDragSkillItemStack(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack droppedItemStack = event.getOldCursor();
		PlayerClass playerClass = pc.getPlayerClass();
		Skill skill = playerClass.skillForHotbarItemStack(droppedItemStack);
		if (skill == null) {
			return;
		}
		int slot = (int) event.getRawSlots().toArray()[0];
		if (slot == 36) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.GRAY + "The first slot of your hotbar is reserved for your weapon");
		} else if (slot < 36 || slot > 44) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.GRAY + "Skills can only be placed on your hotbar");
		}
	}

	@EventHandler
	private void onCloseSkillTree(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		PlayerClass playerClass = pc.getPlayerClass();
		SkillTree skilltree = playerClass.getSkillTree();
		if (skilltree.inventoryMap.get(pc) == event.getInventory()) {
			skilltree.inventoryMap.remove(pc);
		}
	}

	@EventHandler
	private void onClickInSkillTree(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		PlayerClass playerClass = pc.getPlayerClass();
		SkillTree skillTree = playerClass.getSkillTree();
		Inventory skillTreeInventory = skillTree.inventoryMap.get(pc);
		if (skillTreeInventory == null) {
			return;
		}
		Inventory clickedInventory = event.getClickedInventory();
		if (skillTreeInventory != clickedInventory) {
			// when player clicks bottom inventory while skill tree open
			event.setCancelled(true);
			return;
		}
		event.setCancelled(true);
		int slot = event.getSlot();
		int skillRow = slot / 9;
		int skillColumn = slot % 9;
		Skill skill = skillTree.getSkillAt(skillRow, skillColumn);
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
				skillTree.open(pc);
			}

		} else {
			// add to inventory
			if (!skill.isUnlocked(pc)) {
				pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is not unlocked");
			} else {
				ItemStack skillItemStack = skill.getHotbarItemStack();
				Inventory playerInventory = player.getInventory();
				boolean alreadyOnHotbar = false;
				for (int i = 1; i < 9; i++) {
					ItemStack itemStack = playerInventory.getItem(i);
					Skill hotbarSkill = playerClass.skillForHotbarItemStack(itemStack);
					if (skill == hotbarSkill) {
						alreadyOnHotbar = true;
						break;
					}
				}
				if (alreadyOnHotbar) {
					pc.sendMessage(ChatColor.GREEN + skill.getName() + ChatColor.GRAY + " is already on  your hotbar");
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

}
