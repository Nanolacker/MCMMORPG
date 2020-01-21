package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

import net.md_5.bungee.api.ChatColor;

class DiscardItemMenu {

	static final ItemStack CANCEL_ITEM_STACK;
	static final ItemStack CONFIRM_ITEM_STACK;

	private static final Map<PlayerCharacter, DiscardItemMenu> menuMap = new HashMap<>();

	static {
		CANCEL_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Cancel", null, Material.RED_WOOL);
		CONFIRM_ITEM_STACK = ItemFactory.createItemStack(ChatColor.GREEN + "Confirm", null, Material.GREEN_WOOL);
	}

	private final PlayerCharacter pc;
	private final ItemStack itemStack;
	private final boolean reopenInventory;

	public DiscardItemMenu(PlayerCharacter pc, ItemStack itemStack, boolean reopenInventory) {
		this.pc = pc;
		this.itemStack = itemStack;
		this.reopenInventory = reopenInventory;
	}

	static DiscardItemMenu forPC(PlayerCharacter pc) {
		return menuMap.get(pc);
	}

	void open() {
		Inventory inventory = Bukkit.createInventory(pc.getPlayer(), 9,
				"Discard " + itemStack.getItemMeta().getDisplayName());
		inventory.setItem(4, itemStack);
		inventory.setItem(0, CANCEL_ITEM_STACK);
		inventory.setItem(8, CONFIRM_ITEM_STACK);
		pc.getPlayer().openInventory(inventory);
		menuMap.put(pc, this);
	}

	void cancel() {
		Player player = pc.getPlayer();
		player.openInventory(player.getInventory());
		player.setItemOnCursor(itemStack);
		menuMap.remove(pc);
	}

	void confirm() {
		Player player = pc.getPlayer();
		if (reopenInventory) {
			player.openInventory(player.getInventory());
		} else {
			player.closeInventory();
		}
		menuMap.remove(pc);
	}

}
