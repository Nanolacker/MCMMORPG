package com.mcmmorpg.test;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcmmorpg.common.item.GameItem;

import net.md_5.bungee.api.ChatColor;

public class MenuItem extends GameItem {

	public MenuItem() {
		super(makeItemStack());
	}

	private static ItemStack makeItemStack() {
		ItemStack itemStack = new ItemStack(Material.COMPASS);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + "Menu");
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

}
