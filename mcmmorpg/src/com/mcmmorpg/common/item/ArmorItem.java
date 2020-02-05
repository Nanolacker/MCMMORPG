package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class ArmorItem {

	private static final Map<ItemStack, ArmorItem> itemMap = new HashMap<>();

	private final String name;
	private final PlayerClass playerClass;
	private final EquipmentSlot slot;
	private final int level;
	private final double protections;
	private final ItemStack itemStack;

	public ArmorItem(String name, PlayerClass playerClass, EquipmentSlot slot, int level, double protections,
			Material icon, String description) {
		this.name = name;
		this.playerClass = playerClass;
		this.slot = slot;
		this.level = level;
		this.protections = protections;
		this.itemStack = ItemFactory.createItemStack(name, description, icon);
		itemMap.put(itemStack, this);
	}
	
	public static ArmorItem forItemStack(ItemStack itemStack) {
		return itemMap.get(itemStack);
	}

	public String getName() {
		return name;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public int getLevel() {
		return level;
	}

	public double getProtections() {
		return protections;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

}
