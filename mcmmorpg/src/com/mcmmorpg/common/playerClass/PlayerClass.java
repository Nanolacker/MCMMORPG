package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.event.EventManager;

/**
 * PlayerClasses can be made using JSON. Code will need to be written that deals
 * with the class's skill use, however.
 */
public final class PlayerClass {
    /**
     * Completely arbitrarily assigned.
     */
    private static final Material HOTBAR_ITEM_STACK_MATERIAL = Material.GRASS;

    /**
     * Keys are the names of the classes.
     */
    private static final Map<String, PlayerClass> playerClasses;

    private final String name;
    private final Skill[] skills;

    private transient Map<ItemStack, Skill> hotbarItemStackMap = new HashMap<>();
    private transient SkillTree skillTree;

    static {
        playerClasses = new HashMap<>();
        EventManager.registerEvents(new PlayerClassListener());
    }

    /**
     * Creates a new player class. Functionality for skill use will need to be
     * handles elsewhere though events.
     */
    public PlayerClass(String name, Skill[] skills) {
        this.name = name;
        this.skills = skills;
    }

    /**
     * Returns the player class of the specified name.
     */
    public static PlayerClass forName(String name) {
        return playerClasses.get(name);
    }

    /**
     * Sets up this player class. This must be called after construction!
     */
    public void initialize() {
        hotbarItemStackMap = new HashMap<>();
        for (Skill skill : skills) {
            skill.initialize(this);
            ItemStack hotbarItemStack = skill.getHotbarItemStack().clone();
            hotbarItemStack.setType(HOTBAR_ITEM_STACK_MATERIAL);
            hotbarItemStackMap.put(hotbarItemStack, skill);
        }
        this.skillTree = new SkillTree(this);
        playerClasses.put(name, this);
    }

    /**
     * Returns the name of this player class.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the skills of this player class.
     */
    public Skill[] getSkills() {
        return skills;
    }

    /**
     * Returns this class's skill tree.
     */
    public SkillTree getSkillTree() {
        return skillTree;
    }

    /**
     * Returns the skill belonging to this class of the specified name. Returns null
     * if no skill with the specified name exists.
     */
    public Skill skillForName(String skillName) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName)) {
                return skill;
            }
        }
        return null;
    }

    /**
     * Returns the skill corresponding to a skill item stack on a player character's
     * hotbar.
     */
    Skill skillForHotbarItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemStack unitItemStack = itemStack.clone();
        unitItemStack.setType(HOTBAR_ITEM_STACK_MATERIAL);
        unitItemStack.setAmount(1);
        return hotbarItemStackMap.get(unitItemStack);
    }
}
