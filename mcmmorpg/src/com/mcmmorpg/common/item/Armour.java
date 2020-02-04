package com.mcmmorpg.common.item;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class Armour {

	private String name;
	private final PlayerClass playerClass;
	private final EquipmentSlot slot;
	private final int level;
	private final double protections;

	private final ItemStack itemStack;

	public Armour(String name, PlayerClass playerClass, EquipmentSlot slot, int level, double protections,
			Material icon, String description) {
		this.name = name;
		this.playerClass = playerClass;
		this.slot = slot;
		this.level = level;
		this.protections = protections;

		this.itemStack = ItemFactory.createItemStack(name, description, icon);
	}

}
