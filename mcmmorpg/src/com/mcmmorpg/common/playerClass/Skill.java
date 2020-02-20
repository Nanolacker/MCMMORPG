package com.mcmmorpg.common.playerClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

public final class Skill implements Listener {

	private static final double COOLDOWN_UPDATE_PERIOD_SECONDS = 0.1;
	private static final Material LOCKED_MATERIAL = Material.BARRIER;
	private static final Noise SKILL_USE_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

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
		EventManager.registerEvents(this);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
			pc.sendMessage(ChatColor.GREEN + name + " unlocked!");
		} else {
			// upgrade
			int newLevel = data.getUpgradeLevel() + 1;
			data.setUpgradeLevel(newLevel);
			pc.sendMessage(name + " upgraded to level " + newLevel);
		}
	}

	ItemStack getHotbarItemStack() {
		return hotbarItemStack;
	}

	private ItemStack createHotbarItemStack() {
		StringBuilder lore = new StringBuilder();
		lore.append(ChatColor.GOLD + "Level " + minimumLevel + " skill");
		lore.append(ChatColor.AQUA + "\nCost: " + manaCost);
		lore.append(ChatColor.YELLOW + "\nCooldown: " + cooldown);
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
		lore.append(ChatColor.GOLD + "\nUpgraded " + upgradeLevel + "/" + maximumUpgradeLevel);
		lore.append(ChatColor.AQUA + "\nCost: " + manaCost);
		lore.append(ChatColor.YELLOW + "\nCooldown: " + cooldown);
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
					lore.append("\nShift click to upgrade (1 skill point)");
				}
			} else {
				lore.append("\nShift click to unlock (1 skill point)");
			}
		}

		ItemStack itemStack = ItemFactory.createItemStack(ChatColor.GREEN + name, lore.toString(), material);
		return itemStack;
	}

	@EventHandler
	private void onChangeHeldItem(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		if (pc.getPlayerClass() != this.playerClass) {
			return;
		}
		Inventory inventory = player.getInventory();
		int slot = event.getNewSlot();
		ItemStack itemStack = inventory.getItem(slot);
		if (itemStack == null) {
			return;
		}
		ItemStack sizeOfOne = itemStack.clone();
		sizeOfOne.setAmount(1);
		if (sizeOfOne.equals(hotbarItemStack)) {
			if (isOnCooldown(pc)) {
				pc.sendMessage(ChatColor.RED + "On cooldown! (" + (int) Math.ceil(getCooldown(pc)) + ")");
				SKILL_USE_NOISE.play(player);
			} else if (pc.getCurrentMana() < manaCost) {
				pc.sendMessage(ChatColor.RED + "Insufficient " + ChatColor.AQUA + "MP" + ChatColor.RED + "!");
				SKILL_USE_NOISE.play(player);
			} else {
				this.use(pc);
			}
		}
	}

	private void use(PlayerCharacter pc) {
		SkillUseEvent event = new SkillUseEvent(pc, this);
		EventManager.callEvent(event);
		pc.setCurrentMana(pc.getCurrentMana() - manaCost);
		cooldown(pc, cooldown);
		SKILL_USE_NOISE.play(pc);
		pc.sendMessage(ChatColor.YELLOW + "Used " + ChatColor.GREEN + name + ChatColor.YELLOW + "! " + ChatColor.AQUA
				+ -manaCost + " MP");
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
				updateItemStack(pc, newCooldown);
			}
		};
		cooldownTask.schedule();
	}

	/**
	 * Displays the updated cooldown.
	 */
	private void updateItemStack(PlayerCharacter pc, double cooldownSeconds) {
		Inventory inventory = pc.getInventory();
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack == null) {
				continue;
			}
			ItemStack sizeOfOne = itemStack.clone();
			sizeOfOne.setAmount(1);
			if (sizeOfOne.equals(this.hotbarItemStack)) {
				int newAmount = (int) Math.ceil(cooldownSeconds);
				if (itemStack.getAmount() != newAmount) {
					itemStack.setAmount(newAmount);
				}
				return;
			}
		}
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

}
