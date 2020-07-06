package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Button {

	static Map<ItemStack, Button> buttonMap = new HashMap<>();

	private final ItemStack itemStack;

	protected Button(String name, String description, Material icon) {
		itemStack = ItemFactory.createItemStack(name, description, icon);
		buttonMap.put(itemStack, this);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	protected abstract void onInteract(Player player);

	public void unregister() {
		buttonMap.remove(itemStack);
	}

}
