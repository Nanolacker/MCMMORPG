package com.mcmmorpg.common.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.util.BukkitUtility;

/**
 * An object to be clicked in an inventory view like a button.
 */
public abstract class Button {
    private static Map<ItemStack, Button> buttonMap = new HashMap<>();

    private final ItemStack itemStack;

    protected Button(String name, String description, Material icon) {
        itemStack = BukkitUtility.createItemStack(name, description, icon);
        buttonMap.put(itemStack, this);
    }

    /**
     * Returns the button that corresponds to the item stack.
     */
    public static Button forItemStack(ItemStack itemStack) {
        return buttonMap.get(itemStack);
    }

    /**
     * Returns the item stack corresponding to this button.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    protected abstract void onInteract(Player player);

    /**
     * Unregister this button.
     */
    public void unregister() {
        buttonMap.remove(itemStack);
    }
}
