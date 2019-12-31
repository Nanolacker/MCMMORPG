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

public class Skill implements Listener {

	private static final double COOLDOWN_UPDATE_PERIOD_SECONDS = 0.1;

	private final String name;
	private final String description;
	private final int level;
	private final int skillTreeRow;
	private final int skillTreeColumn;
	private final Material icon;
	private transient PlayerClass playerClass;
	private final transient ItemStack itemStack;

	public Skill(String name, String description, int level, int skillTreeRow, int skillTreeColumn, Material icon) {
		this.name = name;
		this.description = description;
		this.level = level;
		this.skillTreeRow = skillTreeRow;
		this.skillTreeColumn = skillTreeColumn;
		this.icon = icon;
		itemStack = createItemStack();
	}

	private ItemStack createItemStack() {
		ItemStack item = ItemStackFactory.create(name, description, icon);
		return item;
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

	public int getLevel() {
		return level;
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

	ItemStack getHotbarItemStack() {
		return itemStack;
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
		Inventory inventory = player.getInventory();
		int slot = event.getNewSlot();
		if (inventory.getItem(slot) == itemStack) {
			this.use(pc);
			event.setCancelled(true);
		}
	}

	public final void use(PlayerCharacter pc) {
		if (getUpgradeLevel(pc) == 0) {
			throw new IllegalArgumentException("Player has not unlocked skill");
		}
		SkillUseEvent event = new SkillUseEvent(pc, this);
		EventManager.callEvent(event);
	}

	public void cooldown(PlayerCharacter pc, double duration) {
		RepeatingTask cooldownTask = new RepeatingTask(COOLDOWN_UPDATE_PERIOD_SECONDS) {
			@Override
			public void run() {
				if (!pc.isActive()) {
					cancel();
					return;
				}
				PlayerSkillManager manager = pc.getSkillManager();
				PlayerSkillData data = manager.getSkillData(Skill.this);
				double newCooldown = data.getSkillCooldownSeconds() - COOLDOWN_UPDATE_PERIOD_SECONDS;
				if (newCooldown <= 0) {
					data.setCooldownSeconds(0);
					cancel();
					return;
				}
				data.setCooldownSeconds(newCooldown);
				updateItemStack(pc, newCooldown);
			}
		};
		cooldownTask.schedule();
	}

	private void updateItemStack(PlayerCharacter pc, double cooldownSeconds) {
		Inventory inventory = pc.getInventory();
		for (ItemStack itemStack : inventory.getContents()) {
			if (itemStack.equals(this.itemStack)) {
				itemStack.setAmount((int) cooldownSeconds);
			}
		}
	}

	public double getCooldownSeconds(PlayerCharacter pc) {
		if (getUpgradeLevel(pc) == 0) {
			throw new IllegalArgumentException("Player has not unlocked skill");
		}
		PlayerSkillManager manager = pc.getSkillManager();
		PlayerSkillData data = manager.getSkillData(Skill.this);
		return data.getSkillCooldownSeconds();
	}

	public void denyUse(PlayerCharacter pc, SkillUseDenialReason reason) {
		String message;
		switch (reason) {
		case INSUFFICIENT_MANA:
			message = ChatColor.RED + "Insufficent mana!";
			break;
		case ON_COOLDOWN:
			message = ChatColor.RED + "On cooldown!";
			break;
		default:
			message = null;
			break;
		}
		Noise denyNoise = new Noise(Sound.BLOCK_ANVIL_DESTROY);
		denyNoise.play(pc.getPlayer());
		pc.sendMessage(message);
	}

	public enum SkillUseDenialReason {
		INSUFFICIENT_MANA, ON_COOLDOWN;
	}

}
