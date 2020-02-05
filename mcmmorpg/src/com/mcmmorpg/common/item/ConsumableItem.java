package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class ConsumableItem {

	private static final Map<ItemStack, ConsumableItem> itemMap = new HashMap<>();

	private final String name;
	private final int level;
	private final ItemStack itemStack;

	public ConsumableItem(String name, PlayerClass playerClass, EquipmentSlot slot, int level, double protections,
			Material icon, String description) {
		this.name = name;
		this.level = level;
		this.itemStack = ItemFactory.createItemStack(name, description, icon);
		itemMap.put(itemStack, this);
	}

	public static ConsumableItem forItemStack(ItemStack itemStack) {
		return itemMap.get(itemStack);
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

}
