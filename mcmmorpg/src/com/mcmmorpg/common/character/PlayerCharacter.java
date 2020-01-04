package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillData;
import com.mcmmorpg.common.playerClass.PlayerSkillManager;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.ActionBar;
import com.mcmmorpg.common.ui.SidebarTextArea;
import com.mcmmorpg.common.utils.StringUtils;

public class PlayerCharacter extends CommonCharacter {

	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_WITHER_SPAWN);

	private static final Map<Player, PlayerCharacter> playerMap;

	private final Player player;
	private boolean active;
	private final PlayerClass playerClass;
	private Location respawnLocation;
	private int xp;
	private int skillUpgradePoints;
	private int currency;
	private double currentMana;
	private double maxMana;
	private Quest targetQuest;
	private final PlayerQuestManager questStatusManager;
	private final PlayerSkillManager skillStatusManager;
	private CharacterCollider collider;
	private final MovementSyncer movementSyncer;

	static {
		playerMap = new HashMap<>();
	}

	private PlayerCharacter(Player player, PlayerClass playerClass, Location location, Location respawnLocation, int xp,
			int skillUpgradePoints, int currency, double maxHealth, double currentHealth, double maxMana,
			double currentMana, Quest targetQuest, Quest[] completedQuests, PlayerQuestData[] questData,
			PlayerSkillData[] skillData, ItemStack[] inventoryContents) {
		super(player.getName(), xpToLevel(xp), location, maxHealth);
		this.player = player;
		this.playerClass = playerClass;
		this.respawnLocation = respawnLocation;
		this.xp = xp;
		this.skillUpgradePoints = skillUpgradePoints;
		this.currency = currency;
		setCurrentHealth(currentHealth);
		this.maxMana = maxMana;
		this.currentMana = currentMana;
		this.targetQuest = targetQuest;
		this.questStatusManager = new PlayerQuestManager(completedQuests, questData);
		this.skillStatusManager = new PlayerSkillManager(this, skillData);
		player.getInventory().setContents(inventoryContents);
		this.collider = new PlayerCharacterCollider(this);
		this.movementSyncer = new MovementSyncer(this, player, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);

		movementSyncer.setEnabled(true);
		collider.setActive(true);
		updateQuestDisplay();
		updateXpDisplay();
		updateActionBar();

		player.teleport(getLocation());
		active = true;
		playerMap.put(player, this);
	}

	public static class PlayerCharacterCollider extends CharacterCollider {
		private static final double LENGTH = 1.0, HEIGHT = 2.0, WIDTH = 1.0;

		private PlayerCharacterCollider(PlayerCharacter pc) {
			super(pc, pc.getColliderCenter(), LENGTH, WIDTH, HEIGHT);
		}

		@Override
		protected void onCollisionEnter(Collider other) {
		}

		@Override
		protected void onCollisionExit(Collider other) {
		}

		@Override
		public PlayerCharacter getCharacter() {
			return (PlayerCharacter) super.getCharacter();
		}

	}

	public static List<PlayerCharacter> list() {
		return new ArrayList<PlayerCharacter>(playerMap.values());
	}

	public static PlayerCharacter registerPlayerCharacter(Player player, PlayerCharacterSaveData saveData) {
		PlayerClass playerClass = saveData.getPlayerClass();
		Location location = saveData.getLocation();
		Location respawnLocation = saveData.getRespawnLocation();
		int xp = saveData.getXP();
		int skillUpgradePoints = saveData.getSkillUpgradePoints();
		int currency = saveData.getCurrency();
		double maxHealth = saveData.getMaxHealth();
		double currentHealth = saveData.getCurrentHealth();
		double maxMana = saveData.getMaxHealth();
		double currentMana = saveData.getCurrentMana();
		Quest targetQuest = saveData.getTargetQuest();
		PlayerQuestData[] questStatuses = saveData.getQuestData();
		PlayerSkillData[] skillStatuses = saveData.getSkillStatuses();
		ItemStack[] inventoryContents = saveData.getInventoryContents();
		return new PlayerCharacter(player, playerClass, location, respawnLocation, xp, skillUpgradePoints, currency,
				maxHealth, currentHealth, maxMana, currentMana, targetQuest, saveData.getCompletedQuests(),
				questStatuses, skillStatuses, inventoryContents);
	}

	public static PlayerCharacter forPlayer(Player player) {
		return playerMap.get(player);
	}

	public static boolean playerIsNearby(Location location, double distanceX, double distanceY, double distanceZ) {
		World world = location.getWorld();
		Collection<Entity> nearbyEntities = world.getNearbyEntities(location, distanceX, distanceY, distanceZ);
		for (Entity nearbyEntity : nearbyEntities) {
			if (nearbyEntity.getType().equals(EntityType.PLAYER)) {
				return true;
			}
		}
		return false;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	private static final double MAX_DISTANCE_WITHOUT_PLAYER_TELEPORT = 5.0;

	@Override
	public void setLocation(Location location) {
		Location oldLocation = getLocation();
		double distance = location.distance(oldLocation);
		super.setLocation(location);
		collider.setCenter(getColliderCenter());
		if (distance > MAX_DISTANCE_WITHOUT_PLAYER_TELEPORT) {
			player.teleport(location);
		}
	}

	public Location getRespawnLocation() {
		return respawnLocation;
	}

	public void setRespawnLocation(Location respawnLocation) {
		this.respawnLocation = respawnLocation;
	}

	private void updateActionBar() {
		int rCurrentHealth = (int) Math.round(getCurrentHealth());
		int rMaxHealth = (int) Math.round(getMaxHealth());
		int rCurrentMana = (int) Math.round(currentMana);
		int rMaxMana = (int) Math.round(maxMana);
		int currentLevelXp = getCurrentLevelXp();
		int level = getLevel();
		int targetXp = xpValues[level - 1];
		String text = String.format(
				ChatColor.RED + "HP: %d/%d    " + ChatColor.AQUA + "MP: %d/%d    " + ChatColor.GREEN + "XP: %d/%d",
				rCurrentHealth, rMaxHealth, rCurrentMana, rMaxMana, currentLevelXp, targetXp);
		ActionBar bar = new ActionBar(text);
		bar.apply(player);
	}

	/**
	 * The value at index 0 is the amount of xp it takes to level up from level 1 to
	 * level 2. The value at index 1 is the xp it takes to level up from level 2 to
	 * level 3.
	 */
	private static final int[] xpValues = { 100, 150, 200 };

	public int getXP() {
		return xp;
	}

	public void grantXP(int xp) {
		this.xp += xp;
		checkForLevelUp();
		updateXpDisplay();
		updateActionBar();
	}

	private void updateXpDisplay() {
		int level = getLevel();
		player.setLevel(level);
		int xpTarget = xpValues[level - 1];
		int currentLevelXp = getCurrentLevelXp();
		float levelProgress = (float) currentLevelXp / xpTarget;
		player.setExp(levelProgress);
	}

	/**
	 * Returns how much xp this pc has gained while at its current level.
	 */
	private int getCurrentLevelXp() {
		int currentLevelXp = xp;
		for (int i = 0; i < xpValues.length; i++) {
			if (currentLevelXp >= xpValues[i]) {
				currentLevelXp -= xpValues[i];
			} else {
				break;
			}
		}
		return currentLevelXp;
	}

	private void checkForLevelUp() {
		int newLevel = xpToLevel(xp);
		while (newLevel > getLevel()) {
			levelUp();
		}
	}

	private static int xpToLevel(int xp) {
		for (int i = 0; i < xpValues.length; i++) {
			int xpValue = xpValues[i];
			xp -= xpValue;
			if (xp < 0) {
				return i + 1;
			}
		}
		return maxLevel();
	}

	private void levelUp() {
		setLevel(getLevel() + 1);
		Noise levelUpNoise = new Noise(Sound.ENTITY_PLAYER_LEVELUP);
		levelUpNoise.play(player);
		sendMessage(ChatColor.GREEN + "Level up!");
	}

	public static int maxLevel() {
		return xpValues.length;
	}

	public int getSkillUpgradePoints() {
		return skillUpgradePoints;
	}

	public void setSkillUpgradePoints(int points) {
		this.skillUpgradePoints = points;
	}

	public int getCurrency() {
		return currency;
	}

	public double getMaxMana() {
		return maxMana;
	}

	public double getCurrentMana() {
		return currentMana;
	}

	public Quest getTargetQuest() {
		return targetQuest;
	}

	public void setTargetQuest(Quest targetQuest) {
		if (targetQuest != null && targetQuest.getStatus(this) != QuestStatus.IN_PROGRESS) {
			throw new IllegalArgumentException("Cannot track a quest that is not in progress");
		}
		this.targetQuest = targetQuest;
		updateQuestDisplay();
	}

	public PlayerQuestManager getQuestManager() {
		return questStatusManager;
	}

	public PlayerSkillManager getSkillManager() {
		return skillStatusManager;
	}

	@Override
	protected void die() {
		player.teleport(respawnLocation);
		player.sendTitle(ChatColor.RED + "You died", "respawning...");
		PotionEffect veilEffect = new PotionEffect(PotionEffectType.BLINDNESS, 80, 1);
		player.addPotionEffect(veilEffect);
		DEATH_NOISE.play(player);
		double maxHealth = getMaxHealth();
		setCurrentHealth(maxHealth);
		setAlive(true);
	}

	public void updateQuestDisplay() {
		if (targetQuest == null) {
			SidebarTextArea.clear(player);
		} else {
			String questTitle = ChatColor.GOLD + targetQuest.getName();
			String objectivesText = StringUtils.repeat("-", StringUtils.STANDARD_LINE_LENGTH) + "\n";
			for (QuestObjective objective : targetQuest.getObjectives()) {
				int progress = objective.getProgress(this);
				int goal = objective.getGoal();
				String progressText = "";
				if (progress < goal) {
					progressText = ChatColor.YELLOW + "";
				} else {
					progressText = ChatColor.GREEN + "";
				}
				progressText += progress + "" + ChatColor.WHITE + "/" + ChatColor.GREEN + "" + goal;
				objectivesText += "-" + objective.getDescription() + " " + progressText + ChatColor.RESET + "\n\n";
			}
			SidebarTextArea questDisplay = new SidebarTextArea(questTitle, objectivesText);
			questDisplay.apply(player);
		}
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0.0, 1.0, 0.0);
	}

	private Location getColliderCenter() {
		Location location = getLocation();
		return location.add(0.0, 1.0, 0.0);
	}

	public Player getPlayer() {
		return player;
	}

	public void sendMessage(String message) {
		player.sendMessage(message);
	}

	public Inventory getInventory() {
		return player.getInventory();
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		active = false;
		collider.setActive(false);
		movementSyncer.setEnabled(false);
		playerMap.remove(player);
	}

}
