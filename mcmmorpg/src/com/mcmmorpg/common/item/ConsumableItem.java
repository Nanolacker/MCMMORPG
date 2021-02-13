package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.util.BukkitUtility;

/**
 * An item that can be consumed for a one time use.
 */
public class ConsumableItem extends Item {
    private final int level;

    /**
     * Create a new consumable item. initialize() must be called after construction.
     */
    public ConsumableItem(String name, ItemRarity rarity, Material icon, String description, int level) {
        super(name, rarity, icon, description);
        this.level = level;
    }

    @Override
    protected ItemStack createItemStack() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        StringBuilder lore = new StringBuilder();
        lore.append(rarity.getColor() + rarity.toString() + " Item\n");
        lore.append(ChatColor.GOLD + "Level " + level + "\n\n");
        if (description != null) {
            lore.append(ChatColor.RESET + description + "\n\n");
        }
        lore.append(ChatColor.GRAY + "Shift-click to use");
        return BukkitUtility.createItemStack(rarity.getColor() + getName(), lore.toString(), getIcon());

    }

    /**
     * Returns the minimum level required for a player character to use this
     * consumable item.
     */
    public int getLevel() {
        return level;
    }
}
