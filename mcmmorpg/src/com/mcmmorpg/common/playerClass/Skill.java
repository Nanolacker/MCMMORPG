package com.mcmmorpg.common.playerClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

public final class Skill {

	private static final double COOLDOWN_UPDATE_PERIOD_SECONDS = 0.1;
	private static final Material LOCKED_MATERIAL = Material.BARRIER;
	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);
	private static final Noise UPGRADE_NOISE = new Noise(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
	static final Material DISABLED_MATERIAL = Material.BARRIER;

	private final String name;
	private final String description;
	private final double manaCost;
	/**
	 * In seconds.
	 */
	private final double cooldown;
	private final int minimumLevel;
	private final String prerequisiteSkill;
	private final int maximumUpgradeLevel;
	private final int skillTreeRow;
	private final int skillTreeColumn;
	private final Material icon;
	private transient PlayerClass playerClass;
	private transient ItemStack hotbarItemStack;

	public Skill(String name, String description, int manaCost, int cooldown, int minimumLevel,
			String prerequisiteSkill, int maximumUpgradeLevel, int skillTreeRow, int skillTreeColumn, Material icon) {
		this.name = name;
		this.description = description;
		this.manaCost = manaCost;
		this.cooldown = cooldown;
		this.minimumLevel = minimumLevel;
		this.prerequisiteSkill = prerequisiteSkill;
		this.maximumUpgradeLevel = maximumUpgradeLevel;
		this.skillTreeRow = skillTreeRow;
		this.skillTreeColumn = skillTreeColumn;
		this.icon = icon;
	}

	void initialize(PlayerClass playerClass) {
		this.playerClass = playerClass;
		hotbarItemStack = createHotbarItemStack();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public double getManaCost() {
		return manaCost;
	}

	public int getMinimumLevel() {
		return minimumLevel;
	}

	public Skill getPrerequisiteSkill() {
		return playerClass.skillForName(prerequisiteSkill);
	}

	public int getMaximumUpgradeLevel() {
		return maximumUpgradeLevel;
	}

	public int getSkillTreeRow() {
		return skillTreeRow;
	}

	public int getSkillTreeColumn() {
		return skillTreeColumn;
	}

	public Material getIcon() {
		return icon;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public boolean isUnlocked(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		return data != null;
	}

	public int getUpgradeLevel(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		if (data == null) {
			return 0;
		} else {
			return data.getUpgradeLevel();
		}
	}

	void upgrade(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		if (data == null) {
			// unlock
			manager.unlockSkill(this);
			pc.sendMessage(ChatColor.GREEN + name + ChatColor.GRAY + " unlocked!");
			UPGRADE_NOISE.play(pc);
		} else {
			// upgrade
			int newLevel = data.getUpgradeLevel() + 1;
			data.setUpgradeLevel(newLevel);
			pc.sendMessage(ChatColor.GREEN + name + ChatColor.GRAY + " upgraded to level " + newLevel + "!");
			UPGRADE_NOISE.play(pc);
		}
	}

	ItemStack getHotbarItemStack() {
		return hotbarItemStack;
	}

	private ItemStack createHotbarItemStack() {
		StringBuilder lore = new StringBuilder();
		lore.append(ChatColor.GOLD + playerClass.getName() + " Skill");
		lore.append(ChatColor.AQUA + "\nCost: " + (int) Math.ceil(manaCost));
		lore.append(ChatColor.YELLOW + "\nCooldown: " + (int) Math.ceil(cooldown));
		lore.append(ChatColor.WHITE + "\n\n" + description);
		ItemStack itemStack = ItemFactory.createItemStack(ChatColor.GREEN + name, lore.toString(), icon);
		return itemStack;
	}

	ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		boolean unlocked = isUnlocked(pc);
		int upgradeLevel = data == null ? 0 : data.getUpgradeLevel();
		Material material = unlocked ? icon : LOCKED_MATERIAL;
		StringBuilder lore = new StringBuilder();
		lore.append(ChatColor.GOLD + "Upgraded " + upgradeLevel + "/" + maximumUpgradeLevel);
		lore.append(ChatColor.AQUA + "\nCost: " + (int) Math.ceil(manaCost));
		lore.append(ChatColor.YELLOW + "\nCooldown: " + (int) Math.ceil(cooldown));
		lore.append(ChatColor.WHITE + "\n\n" + description);

		boolean underLevel = pc.getLevel() < minimumLevel;
		Skill prerequisiteSkill0 = playerClass.skillForName(prerequisiteSkill);
		boolean prerequisiteSkillLocked = prerequisiteSkill0 != null && !prerequisiteSkill0.isUnlocked(pc);
		boolean isUnavailable = underLevel || prerequisiteSkillLocked;
		if (isUnavailable) {
			lore.append(ChatColor.RED + "\n");
		}
		if (underLevel) {
			lore.append("\nRequires level " + minimumLevel);
		}
		if (prerequisiteSkillLocked) {
			lore.append("\nRequires " + prerequisiteSkill);
		}

		if (!isUnavailable) {
			lore.append(ChatColor.GRAY + "\n");
			if (unlocked) {
				lore.append(ChatColor.GRAY + "\nClick to add to hotbar");
				if (upgradeLevel < maximumUpgradeLevel) {
					lore.append("\nShift-click to upgrade (1 skill point)");
				}
			} else {
				lore.append("\nShift-click to unlock (1 skill point)");
			}
		}

		ItemStack itemStack = ItemFactory.createItemStack(ChatColor.GREEN + name, lore.toString(), material);
		return itemStack;
	}

	public double getCooldown(PlayerCharacter pc) {
		if (getUpgradeLevel(pc) == 0) {
			throw new IllegalArgumentException("Player has not unlocked skill");
		}
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(Skill.this);
		return data.getSkillCooldownSeconds();
	}

	public boolean isOnCooldown(PlayerCharacter pc) {
		return getCooldown(pc) != 0;
	}

	void cooldown(PlayerCharacter pc, double duration) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		data.setCooldown(duration);
		RepeatingTask cooldownTask = new RepeatingTask(COOLDOWN_UPDATE_PERIOD_SECONDS) {
			@Override
			public void run() {
				if (!pc.isActive()) {
					cancel();
					return;
				}
				double newCooldown = data.getSkillCooldownSeconds() - COOLDOWN_UPDATE_PERIOD_SECONDS;
				if (newCooldown <= 0) {
					data.setCooldown(0);
					cancel();
					return;
				}
				data.setCooldown(newCooldown);
			}
		};
		cooldownTask.schedule();
	}

	public boolean prerequisitesAreMet(PlayerCharacter pc) {
		if (prerequisiteSkill != null) {
			Skill prereq = playerClass.skillForName(prerequisiteSkill);
			if (!prereq.isUnlocked(pc) || pc.getLevel() < minimumLevel) {
				return false;
			}
		}
		return pc.getLevel() >= minimumLevel;
	}

	void use(PlayerCharacter pc) {
		SkillUseEvent event = new SkillUseEvent(pc, this);
		EventManager.callEvent(event);
		pc.setCurrentMana(pc.getCurrentMana() - manaCost);
		cooldown(pc, cooldown);
		CLICK_NOISE.play(pc);
		pc.sendMessage(ChatColor.GRAY + "Used " + ChatColor.GREEN + name + " " + ChatColor.AQUA
				+ -(int) Math.ceil(manaCost) + " MP");
	}

}
