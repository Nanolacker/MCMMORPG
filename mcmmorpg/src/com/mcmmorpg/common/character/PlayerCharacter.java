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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillData;
import com.mcmmorpg.common.playerClass.PlayerSkillManager;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestLog;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.PlayerSoundtrackPlayer;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ActionBarText;
import com.mcmmorpg.common.ui.SidebarText;
import com.mcmmorpg.common.ui.TitleMessage;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.common.utils.StringUtils;

public class PlayerCharacter extends AbstractCharacter {

	/**
	 * The value at index 0 is the amount of xp it takes to level up from level 1 to
	 * level 2. The value at index 1 is the xp it takes to level up from level 2 to
	 * level 3.
	 */
	private static final int[] XP_REQS = { 100, 150, 200 };
	private static final int MAX_XP = getMaxXp();
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_WITHER_SPAWN);

	private static final List<PlayerCharacter> pcs;
	private static final Map<Player, PlayerCharacter> playerMap;

	private final Player player;
	private boolean active;
	private final PlayerClass playerClass;
	private String zone;
	private Location respawnLocation;
	private int xp;
	private int skillUpgradePoints;
	private int currency;
	private double healthRegenRate;
	private double maxMana;
	private double currentMana;
	private double manaRegenRate;
	private Quest targetQuest;
	private final PlayerQuestManager questStatusManager;
	private final PlayerSkillManager skillStatusManager;
	private final PlayerSoundtrackPlayer soundtrackPlayer;
	private CharacterCollider collider;
	private final MovementSyncer movementSyncer;

	static {
		pcs = new ArrayList<>();
		playerMap = new HashMap<>();
		EventManager.registerEvents(new PlayerCharacterListener());
		startRegenTask();
	}

	private PlayerCharacter(Player player, boolean fresh, PlayerClass playerClass, String zone, Location location,
			Location respawnLocation, int xp, int skillUpgradePoints, int currency, double maxHealth,
			double currentHealth, double healthRegenRate, double maxMana, double currentMana, double manaRegenRate,
			Quest targetQuest, Quest[] completedQuests, PlayerQuestData[] questData, PlayerSkillData[] skillData,
			ItemStack[] inventoryContents) {
		super(player.getName(), xpToLevel(xp), location);
		this.player = player;
		this.playerClass = playerClass;
		this.zone = zone;
		this.respawnLocation = respawnLocation;
		this.xp = xp;
		this.skillUpgradePoints = skillUpgradePoints;
		this.currency = currency;
		super.setCurrentHealth(currentHealth);
		super.setMaxHealth(maxHealth);
		this.healthRegenRate = healthRegenRate;
		this.currentMana = currentMana;
		this.maxMana = maxMana;
		this.manaRegenRate = manaRegenRate;
		this.targetQuest = targetQuest;
		this.questStatusManager = new PlayerQuestManager(completedQuests, questData);
		this.skillStatusManager = new PlayerSkillManager(this, skillData);
		this.skillStatusManager.init();
		player.getInventory().setContents(inventoryContents);
		soundtrackPlayer = new PlayerSoundtrackPlayer(player);
		this.collider = new PlayerCharacterCollider(this);
		player.teleport(getLocation());

		this.movementSyncer = new MovementSyncer(this, player, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		movementSyncer.setEnabled(true);
		collider.setActive(true);

		active = true;
		setAlive(true);
		pcs.add(this);
		playerMap.put(player, this);

		updateActionBar();
		updateQuestDisplay();
		updateXpDisplay();
		updateHealthDisplay();
		updateManaDisplay();

		if (fresh) {
			PlayerCharacterLevelUpEvent event = new PlayerCharacterLevelUpEvent(this, 1);
			EventManager.callEvent(event);
		}
	}

	public static class PlayerCharacterCollider extends CharacterCollider {
		private static final double LENGTH = 1.0, HEIGHT = 2.0, WIDTH = 1.0;

		private PlayerCharacterCollider(PlayerCharacter pc) {
			super(pc, pc.getColliderCenter(), LENGTH, HEIGHT, WIDTH);
		}

		@Override
		public PlayerCharacter getCharacter() {
			return (PlayerCharacter) super.getCharacter();
		}
	}

	private static int getMaxXp() {
		int maxXp = 0;
		for (int i = 0; i < XP_REQS.length; i++) {
			maxXp += XP_REQS[i];
		}
		return maxXp;
	}

	private static void startRegenTask() {
		RepeatingTask regenTask = new RepeatingTask(1) {
			@Override
			public void run() {
				for (int i = 0; i < pcs.size(); i++) {
					PlayerCharacter pc = pcs.get(i);
					pc.heal(pc.healthRegenRate, pc);
					pc.setCurrentMana(pc.getCurrentMana() + pc.manaRegenRate);
				}
			}
		};
		regenTask.schedule();
	}

	public static List<PlayerCharacter> getAll() {
		return pcs;
	}

	public static PlayerCharacter registerPlayerCharacter(Player player,
			PersistentPlayerCharacterDataContainer saveData) {
		boolean fresh = saveData.isFresh();
		PlayerClass playerClass = saveData.getPlayerClass();
		String zone = saveData.getZone();
		Location location = saveData.getLocation();
		Location respawnLocation = saveData.getRespawnLocation();
		int xp = saveData.getXP();
		int skillUpgradePoints = saveData.getSkillUpgradePoints();
		int currency = saveData.getCurrency();
		double maxHealth = saveData.getMaxHealth();
		double currentHealth = saveData.getCurrentHealth();
		double healthRegenRate = saveData.getHealthRegenRate();
		double maxMana = saveData.getMaxMana();
		double currentMana = saveData.getCurrentMana();
		double manaRegenRate = saveData.getManaRegenRate();
		Quest targetQuest = saveData.getTargetQuest();
		PlayerQuestData[] questStatuses = saveData.getQuestData();
		PlayerSkillData[] skillStatuses = saveData.getSkillData();
		ItemStack[] inventoryContents = saveData.getInventoryContents();
		return new PlayerCharacter(player, fresh, playerClass, zone, location, respawnLocation, xp, skillUpgradePoints,
				currency, maxHealth, currentHealth, healthRegenRate, maxMana, currentMana, manaRegenRate, targetQuest,
				saveData.getCompletedQuests(), questStatuses, skillStatuses, inventoryContents);
	}

	public static PlayerCharacter forPlayer(Player player) {
		return playerMap.get(player);
	}

	/**
	 * Returns true if there is a player within the specified x, y, and z distances
	 * from the specified location. False otherwise.
	 */
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

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

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
		int targetXp = XP_REQS[level - 1];
		String text = String.format(
				ChatColor.RED + "HP: %d/%d    " + ChatColor.AQUA + "MP: %d/%d    " + ChatColor.GREEN + "XP: %d/%d",
				rCurrentHealth, rMaxHealth, rCurrentMana, rMaxMana, currentLevelXp, targetXp);
		ActionBarText bar = new ActionBarText(text);
		bar.apply(player);
	}

	private void updateHealthDisplay() {
		double proportion = getCurrentHealth() / getMaxHealth();
		player.setHealth(proportion * 20);
	}

	private void updateManaDisplay() {
		double proportion = currentMana / maxMana;
		int foodLevel = (int) Math.ceil(proportion * 20);
		player.setFoodLevel(foodLevel);
	}

	public int getXP() {
		return xp;
	}

	public void grantXp(int xp) {
		this.xp += xp;
		this.xp = (int) MathUtils.clamp(this.xp, 0, MAX_XP);
		sendMessage("+" + xp + " XP!");
		checkForLevelUp();
		updateXpDisplay();
		updateActionBar();
	}

	private void updateXpDisplay() {
		int level = getLevel();
		player.setLevel(level);
		int xpTarget = XP_REQS[level - 1];
		int currentLevelXp = getCurrentLevelXp();
		float levelProgress = (float) currentLevelXp / xpTarget;
		player.setExp(levelProgress);
	}

	/**
	 * Returns how much xp this pc has gained while at its current level.
	 */
	private int getCurrentLevelXp() {
		int currentLevelXp = xp;
		for (int i = 0; i < XP_REQS.length; i++) {
			if (currentLevelXp >= XP_REQS[i]) {
				currentLevelXp -= XP_REQS[i];
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
		for (int i = 0; i < XP_REQS.length; i++) {
			int xpValue = XP_REQS[i];
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
		return XP_REQS.length;
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

	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
		updateActionBar();
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		updateActionBar();
		updateHealthDisplay();
	}

	@Override
	public void setMaxHealth(double maxHealth) {
		super.setMaxHealth(maxHealth);
		updateActionBar();
		updateHealthDisplay();
	}

	/**
	 * In health per second.
	 */
	public double getHealthRegenRate() {
		return healthRegenRate;
	}

	/**
	 * In health per second.
	 */
	public void setHealthRegenRate(double healthRegenRate) {
		this.healthRegenRate = healthRegenRate;
	}

	public double getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(double currentMana) {
		currentMana = MathUtils.clamp(currentMana, 0, maxMana);
		this.currentMana = currentMana;
		updateActionBar();
		updateManaDisplay();
	}

	/**
	 * In mana per second.
	 */
	public double getManaRegenRate() {
		return manaRegenRate;
	}

	/**
	 * In mana per second.
	 */
	public void setManaRegenRate(double manaRegenRate) {
		this.manaRegenRate = manaRegenRate;
	}

	@Override
	public void damage(double amount, Source source) {
		amount = getMitigatedDamage(amount, getProtections());
		super.damage(amount, source);
		// for effect
		player.damage(0);
	}

	private static double getMitigatedDamage(double damage, double protections) {
		double damageMultiplier = damage / (damage + protections);
		return damage * damageMultiplier;
	}

	public double getProtections() {
		double protections = 0;
		ArmorItem head = getHeadArmor();
		if (head != null) {
			protections += head.getProtections();
		}
		ArmorItem chest = getChestArmor();
		if (chest != null) {
			protections += chest.getProtections();
		}
		ArmorItem legs = getLegArmor();
		if (legs != null) {
			protections += legs.getProtections();
		}
		ArmorItem feet = getFeetArmor();
		if (feet != null) {
			protections += feet.getProtections();
		}
		return protections;
	}

	public ArmorItem getHeadArmor() {
		ItemStack itemStack = getInventory().getItem(103);
		return ArmorItem.forItemStack(itemStack);
	}

	public ArmorItem getChestArmor() {
		ItemStack itemStack = getInventory().getItem(102);
		return ArmorItem.forItemStack(itemStack);
	}

	public ArmorItem getLegArmor() {
		ItemStack itemStack = getInventory().getItem(101);
		return ArmorItem.forItemStack(itemStack);
	}

	public ArmorItem getFeetArmor() {
		ItemStack itemStack = getInventory().getItem(100);
		return ArmorItem.forItemStack(itemStack);
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

	public void openQuestLog() {
		QuestLog questLog = new QuestLog(this);
		questLog.open();
	}

	public PlayerSkillManager getSkillManager() {
		return skillStatusManager;
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		player.teleport(respawnLocation);
		TitleMessage deathMessage = new TitleMessage("You died", "respawning...");
		deathMessage.sendTo(player);
		PotionEffect veilEffect = new PotionEffect(PotionEffectType.BLINDNESS, 80, 1);
		player.addPotionEffect(veilEffect);
		DEATH_NOISE.play(player);
		player.teleport(respawnLocation);
		setAlive(true);
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		if (other instanceof PlayerCharacter) {
			return true;
		} else {
			return false;
		}
	}

	public void updateQuestDisplay() {
		if (targetQuest == null) {
			SidebarText.clear(player);
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
				objectivesText += progressText + " " + ChatColor.RESET + objective.getDescription() + ChatColor.RESET
						+ "\n";
			}
			SidebarText questDisplay = new SidebarText(questTitle, objectivesText);
			questDisplay.apply(player);
		}
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

	/**
	 * Use this to set the song playing to the player.
	 */
	public PlayerSoundtrackPlayer getSoundTrackPlayer() {
		return soundtrackPlayer;
	}

	/**
	 * Returns whether this player character is currently being used by a player.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * This needs to be invoked when the player abandons this player character.
	 */
	public void deactivate() {
		active = false;
		collider.setActive(false);
		movementSyncer.setEnabled(false);
		playerMap.remove(player);
	}

	/**
	 * Handles events pertaining to player characters.
	 */
	private static class PlayerCharacterListener implements Listener {
		@EventHandler
		private void onPlayerDamage(EntityDamageEvent event) {
			// Screw it. Let's just put it here for all entities.
			event.setDamage(0);
		}
	}

}
