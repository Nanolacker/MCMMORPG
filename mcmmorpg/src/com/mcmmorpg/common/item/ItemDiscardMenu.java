package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

import net.md_5.bungee.api.ChatColor;

class ItemDiscardMenu {

	static final ItemStack CANCEL_ITEM_STACK;
	static final ItemStack CONFIRM_ITEM_STACK;

	private static final Map<PlayerCharacter, ItemDiscardMenu> menuMap = new HashMap<>();

	static {
		CANCEL_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Cancel", null, Material.RED_WOOL);
		CONFIRM_ITEM_STACK = ItemFactory.createItemStack(ChatColor.GREEN + "Confirm", null, Material.GREEN_WOOL);
	}

	final PlayerCharacter pc;
	final ItemStack itemStack;

	public ItemDiscardMenu(PlayerCharacter pc, ItemStack itemStack) {
		this.pc = pc;
		this.itemStack = itemStack;
		menuMap.put(pc, this);
	}

	void open() {
		Inventory inventory = Bukkit.createInventory(pc.getPlayer(), 9,
				"Discard " + itemStack.getItemMeta().getDisplayName());
		inventory.setItem(4, itemStack);
		inventory.setItem(0, CANCEL_ITEM_STACK);
		inventory.setItem(8, CONFIRM_ITEM_STACK);
		pc.getPlayer().openInventory(inventory);
	}

	public static ItemDiscardMenu forPC(PlayerCharacter pc) {
		return menuMap.get(pc);
	}

}
