package com.mcmmorpg.common.playerClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.ItemStackFactory;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

public final class Skill implements Listener {

	private static final double COOLDOWN_UPDATE_PERIOD_SECONDS = 0.1;

	private final String name;
	private final String description;
	private final double manaCost;
	/**
	 * In seconds.
	 */
	private final double cooldown;
	private final int minimumLevel;
	private final String prerequisiteSkill;
	private final int skillTreeRow;
	private final int skillTreeColumn;
	private final Material icon;
	private transient PlayerClass playerClass;
	private final transient ItemStack hotbarItemStack;

	public Skill(String name, String description, int manaCost, int cooldown, int minimumLevel,
			String prerequisiteSkill, int skillTreeRow, int skillTreeColumn, Material icon) {
		this.name = name;
		this.description = description;
		this.manaCost = manaCost;
		this.cooldown = cooldown;
		this.minimumLevel = minimumLevel;
		this.prerequisiteSkill = prerequisiteSkill;
		this.skillTreeRow = skillTreeRow;
		this.skillTreeColumn = skillTreeColumn;
		this.icon = icon;
<<<<<<< HEAD
		itemStack = createItemStack();
	}

	private ItemStack createItemStack() {
		ItemStack item = ItemStackFactory.create(name, "level " + minimumLevel + "\n" + description, icon);
		return item;
=======
		this.hotbarItemStack = ItemStackFactory.create(name, description, icon);
>>>>>>> branch 'master' of https://github.com/Nanolacker/MCMMORPG.git
	}

	void initialize(PlayerClass playerClass) {
		this.playerClass = playerClass;
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

	Skill getPrerequisiteSkill() {
		return playerClass.skillForName(prerequisiteSkill);
	}

	int getSkillTreeRow() {
		return skillTreeRow;
	}

	int getSkillTreeColumn() {
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
		int newLevel = data.getUpgradeLevel() + 1;
		data.setUpgradeLevel(newLevel);
	}

	ItemStack getHotbarItemStack() {
		return hotbarItemStack;
	}

	ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(this);
		int level = data == null ? 0 : data.getUpgradeLevel();
		ItemStack itemStack = ItemStackFactory.create(name + " level " + level, description, icon);
		return itemStack;
	}

	@EventHandler
	private void onUse(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			throw new IllegalStateException("Player not connected to a player character");
		}
		if (pc.getPlayerClass() != this.playerClass) {
			return;
		}
		Inventory inventory = player.getInventory();
		int slot = event.getNewSlot();
<<<<<<< HEAD
		if (inventory.getItem(slot) == itemStack) {
=======
		if (inventory.getItem(slot) == hotbarItemStack) {
			this.use(pc);
>>>>>>> branch 'master' of https://github.com/Nanolacker/MCMMORPG.git
			event.setCancelled(true);
			if (isOnCooldown(pc)) {
				pc.sendMessage("On cooldown!");
			} else if (pc.getCurrentMana() < manaCost) {
				pc.sendMessage("Insufficient mana!");
			} else {
				this.use(pc);
			}
		}
	}

	private void use(PlayerCharacter pc) {
		SkillUseEvent event = new SkillUseEvent(pc, this);
		EventManager.callEvent(event);
		pc.setMaxHealth(pc.getCurrentMana() - manaCost);
		cooldown(pc);
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

	private void cooldown(PlayerCharacter pc) {
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(Skill.this);
		data.setCooldown(cooldown);
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

	private void updateItemStack(PlayerCharacter pc, double cooldownSeconds) {
		Inventory inventory = pc.getInventory();
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack.equals(this.hotbarItemStack)) {
				itemStack.setAmount((int) cooldownSeconds);
				return;
			}
		}
	}

}
