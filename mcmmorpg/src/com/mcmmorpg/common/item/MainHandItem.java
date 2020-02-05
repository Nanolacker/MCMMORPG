package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class MainHandItem {

	static final Map<ItemStack, MainHandItem> itemMap = new HashMap<>();

	private final String name;
	private final PlayerClass playerClass;
	private final int level;
	private final ItemStack itemStack;

	public MainHandItem(String name, PlayerClass playerClass, int level, Material icon, String description) {
		this.name = name;
		this.playerClass = playerClass;
		this.level = level;
		this.itemStack = ItemFactory.createItemStack(name, description, icon);
		itemMap.put(itemStack, this);
	}

	public String getName() {
		return name;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public int getLevel() {
		return level;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

}
