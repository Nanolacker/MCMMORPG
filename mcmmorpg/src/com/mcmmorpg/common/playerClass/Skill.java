package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

public class Skill {

	private static final double COOLDOWN_UPDATE_PERIOD_SECONDS = 0.1;

	private static final Map<PlayerCharacter, List<Skill>> cooldownMap;

	private final String name;
	private final int level;
	private transient PlayerClass playerClass;
	private final int skillTreeRow;
	private final int skillTreeColumn;

	static {
		cooldownMap = new HashMap<>();
	}

	public Skill(String name, int level, int skillTreeRow, int skillTreeColumn) {
		this.name = name;
		this.level = level;
		this.skillTreeRow = skillTreeRow;
		this.skillTreeColumn = skillTreeColumn;
	}

	void initialize(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public final void use(PlayerCharacter pc) {

	}

	protected void cooldown(PlayerCharacter pc, double duration) {
		RepeatingTask cooldownTask = new RepeatingTask(COOLDOWN_UPDATE_PERIOD_SECONDS) {
			@Override
			public void run() {
				if (!pc.isActive()) {
					cancel();
					return;
				}
				SkillStatusManager manager = pc.getSkillStatusManager();
				PlayerSkillStatus status = manager.getSkillStatus(Skill.this);
				double newCooldown = status.getSkillCooldownSeconds() - COOLDOWN_UPDATE_PERIOD_SECONDS;
				if (newCooldown <= 0) {
					status.setCooldownSeconds(0);
					cancel();
					return;
				}
				status.setCooldownSeconds(newCooldown);
				// don't forget to update ItemStack!!!!!
			}
		};
		cooldownTask.schedule();
	}

	protected void denyUse(PlayerCharacter pc, SkillUseDenialReason reason) {
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

	protected enum SkillUseDenialReason {
		INSUFFICIENT_MANA, ON_COOLDOWN;
	}

}
