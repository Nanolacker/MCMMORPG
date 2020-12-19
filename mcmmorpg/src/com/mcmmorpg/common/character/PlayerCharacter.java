package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.ai.MotionSynchronizer;
import com.mcmmorpg.common.ai.MotionSynchronizer.MotionSynchronizerMode;
import com.mcmmorpg.common.event.CharacterKillEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterReceiveItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterRegisterEvent;
import com.mcmmorpg.common.event.PlayerCharacterRemoveEvent;
import com.mcmmorpg.common.event.PlayerCharacterRemoveItemEvent;
import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.physics.RaycastHit;
import com.mcmmorpg.common.playerClass.PlayerCharacterSkillData;
import com.mcmmorpg.common.playerClass.PlayerCharacterSkillManager;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.PlayerCharacterQuestData;
import com.mcmmorpg.common.quest.PlayerCharacterQuestManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestLog;
import com.mcmmorpg.common.social.Party;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.PlayerCharacterSoundtrackPlayer;
import com.mcmmorpg.common.time.Clock;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ActionBarText;
import com.mcmmorpg.common.ui.PlayerCharacterInteractionCollider;
import com.mcmmorpg.common.ui.PlayerCharacterMap;
import com.mcmmorpg.common.ui.SidebarText;
import com.mcmmorpg.common.ui.TitleText;
import com.mcmmorpg.common.util.CardinalDirection;
import com.mcmmorpg.common.util.MathUtility;

/**
 * Represents a player character.
 */
public final class PlayerCharacter extends Character {

	/**
	 * The value at index 0 is the amount of xp it takes to level up from level 1 to
	 * level 2. The value at index 1 is the xp it takes to level up from level 2 to
	 * level 3.
	 */
	private static final int[] XP_REQS = { 100, 150, 225, 325, 450, 600, 775, 975, 1200, 1450, 1725, 2025, 2350, 2700,
			3075, 3475, 3900, 4350, 4825 };
	private static final int MAX_XP = determineMaxXp();
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PLAYER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_WITHER_SPAWN);
	private static final double HEIGHT = 2.0;
	private static final double MAX_DISPLACEMENT_WITHOUT_TELEPORT_SQUARED = 25.0;
	private static final double INTERACT_RANGE = 4.0;
	private static final double INTERACT_COOLDOWN = 0.1;
	private static final double SPEAK_RADIUS = 25.0;
	private static final double SKILL_UPGRADE_REMINDER_PERIOD = 30.0;

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
	private final PlayerCharacterQuestManager questStatusManager;
	private final QuestLog questLog;
	private final PlayerCharacterSkillManager skillStatusManager;
	private final List<String> tags;
	/**
	 * The total play time accumulated on this player character before the current
	 * play session.
	 */
	private final double previousPlayTime;
	private final double playSessionStartTime;
	private final PlayerCharacterSoundtrackPlayer soundtrackPlayer;
	private final PlayerCharacterMap map;
	private CharacterCollider hitbox;
	private final MotionSynchronizer motionSyncer;
	private DelayedTask undisarmTask;
	private DelayedTask unsilenceTask;
	private boolean isDisarmed;
	private boolean isSilenced;
	private Party party;

	static {
		pcs = new ArrayList<>();
		playerMap = new HashMap<>();
		EventManager.registerEvents(new PlayerCharacterListener());
		scheduleRegenTask();
		scheduleSkillUpgradeReminderTask();
	}

	/**
	 * Creates a new character with the specified values.
	 */
	private PlayerCharacter(Player player, boolean fresh, PlayerClass playerClass, String zone, Location location,
			Location respawnLocation, int xp, int skillUpgradePoints, int currency, double maxHealth,
			double currentHealth, double healthRegenRate, double maxMana, double currentMana, double manaRegenRate,
			Quest[] completedQuests, PlayerCharacterQuestData[] questData, PlayerCharacterSkillData[] skillData,
			ItemStack[] inventoryContents, String[] tags, double playTime) {
		super(ChatColor.GREEN + player.getName(), xpToLevel(xp), location);
		super.setHeight(HEIGHT);
		this.player = player;
		this.playerClass = playerClass;
		this.zone = zone;
		this.respawnLocation = respawnLocation;
		this.xp = xp;
		this.skillUpgradePoints = skillUpgradePoints;
		this.currency = currency;
		super.setMaxHealth(maxHealth);
		this.healthRegenRate = healthRegenRate;
		this.currentMana = currentMana;
		this.maxMana = maxMana;
		this.manaRegenRate = manaRegenRate;
		this.questStatusManager = new PlayerCharacterQuestManager(completedQuests, questData);
		this.questLog = new QuestLog(this);
		this.skillStatusManager = new PlayerCharacterSkillManager(this, skillData);
		this.skillStatusManager.init();
		this.player.getInventory().setContents(inventoryContents);
		this.tags = new ArrayList<>(Arrays.asList(tags));
		this.previousPlayTime = playTime;
		this.playSessionStartTime = Clock.getTime();

		this.player.teleport(getLocation());
		this.soundtrackPlayer = new PlayerCharacterSoundtrackPlayer(this);
		this.map = new PlayerCharacterMap(this);
		this.hitbox = new PlayerCharacterCollider(this);

		this.motionSyncer = new MotionSynchronizer(this, MotionSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		this.motionSyncer.setEntity(player);
		this.motionSyncer.setEnabled(true);

		this.undisarmTask = null;
		this.unsilenceTask = null;
		this.isDisarmed = false;
		this.isSilenced = false;
		this.party = null;

		this.active = true;
		setAlive(true);
		super.setCurrentHealth(currentHealth);
		pcs.add(this);
		playerMap.put(player, this);

		hitbox.setActive(true);
		updateActionBar();
		questLog.updateSidebarText();
		updateXpDisplay();
		updateHealthDisplay();
		updateManaDisplay();
		hidePlayerNameplate();
		setNameplateVisible(true);
		setHealthBarVisible(true);

		if (fresh) {
			setSkillUpgradePoints(1);
			PlayerCharacterLevelUpEvent event = new PlayerCharacterLevelUpEvent(this, 1);
			EventManager.callEvent(event);
		}
	}

	/**
	 * Hitbox for player characters.
	 */
	public static final class PlayerCharacterCollider extends CharacterCollider {
		private static final double LENGTH = 1.0, HEIGHT = 2.0, WIDTH = 1.0;

		private PlayerCharacterCollider(PlayerCharacter pc) {
			super(pc, pc.getColliderCenter(), LENGTH, HEIGHT, WIDTH);
		}

		@Override
		public PlayerCharacter getCharacter() {
			return (PlayerCharacter) super.getCharacter();
		}
	}

	/**
	 * Calculates the maximum xp attainable by a player character.
	 */
	private static int determineMaxXp() {
		int maxXp = 0;
		for (int i = 0; i < XP_REQS.length; i++) {
			maxXp += XP_REQS[i];
		}
		return maxXp;
	}

	/**
	 * Schedules a task to regenerate player character health and mana periodically.
	 */
	private static void scheduleRegenTask() {
		RepeatingTask regenTask = new RepeatingTask(1) {
			@Override
			protected void run() {
				for (int i = 0; i < pcs.size(); i++) {
					PlayerCharacter pc = pcs.get(i);
					pc.heal(pc.healthRegenRate, pc);
					pc.setCurrentMana(pc.getCurrentMana() + pc.manaRegenRate);
				}
			}
		};
		regenTask.schedule();
	}

	/**
	 * Schedules a task to remind player characters to use their skill upgrade
	 * points periodically.
	 */
	private static void scheduleSkillUpgradeReminderTask() {
		RepeatingTask skillUpgradeReminderTask = new RepeatingTask(SKILL_UPGRADE_REMINDER_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < pcs.size(); i++) {
					PlayerCharacter pc = pcs.get(i);
					int skillUpgradePoints = pc.getSkillUpgradePoints();
					if (skillUpgradePoints > 0) {
						pc.sendMessage(ChatColor.GREEN + "You have " + skillUpgradePoints + " skill "
								+ (skillUpgradePoints == 1 ? "point" : "points")
								+ " available! Open your skill tree to unlock or upgrade a skill!");
					}
				}
			}
		};
		skillUpgradeReminderTask.schedule();
	}

	/**
	 * Returns a list of all player characters in the game.
	 */
	public static List<PlayerCharacter> listAll() {
		return pcs;
	}

	/**
	 * Creates a new player character from the specified save data.
	 */
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
		PlayerCharacterQuestData[] questStatuses = saveData.getQuestData();
		PlayerCharacterSkillData[] skillStatuses = saveData.getSkillData();
		ItemStack[] inventoryContents = saveData.getInventoryContents();
		String[] tags = saveData.getTags();
		double playTime = saveData.getTotalPlayTime();

		PlayerCharacter pc = new PlayerCharacter(player, fresh, playerClass, zone, location, respawnLocation, xp,
				skillUpgradePoints, currency, maxHealth, currentHealth, healthRegenRate, maxMana, currentMana,
				manaRegenRate, saveData.getCompletedQuests(), questStatuses, skillStatuses, inventoryContents, tags,
				playTime);
		PlayerCharacterRegisterEvent event = new PlayerCharacterRegisterEvent(pc);
		EventManager.callEvent(event);
		return pc;
	}

	/**
	 * Returns the player character that corresponds to the specified player.
	 */
	public static PlayerCharacter forPlayer(Player player) {
		return playerMap.get(player);
	}

	/**
	 * Returns a list of all player characters in the area specified by the location
	 * and radius.
	 */
	public static List<PlayerCharacter> getNearbyPlayerCharacters(Location location, double radius) {
		List<PlayerCharacter> pcs = new ArrayList<>();
		double diameter = radius * 2;
		Collider bounds = new Collider(location, diameter, diameter, diameter);
		bounds.setActive(true);
		Collider[] contacts = bounds.getContacts();
		bounds.setActive(false);
		for (Collider contact : contacts) {
			if (contact instanceof PlayerCharacterCollider) {
				PlayerCharacter pc = ((PlayerCharacterCollider) contact).getCharacter();
				pcs.add(pc);
			}
		}
		return pcs;
	}

	/**
	 * Returns true if there is a player within the specified x, y, and z distances
	 * from the specified location. False otherwise.
	 */
	public static boolean playerCharacterIsNearby(Location location, double radius) {
		List<PlayerCharacter> nearbyPcs = getNearbyPlayerCharacters(location, radius);
		return nearbyPcs.size() > 0;
	}

	/**
	 * Returns this player character's class.
	 */
	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	/**
	 * Returns the zone this player character is currently in.
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * Sets the zone this player character is currently in.
	 */
	public void setZone(String zone) {
		if (!active) {
			return;
		}
		if (zone.equals(this.zone)) {
			return;
		} else {
			this.zone = zone;
			sendMessage(ChatColor.GRAY + "Entering " + zone);
		}
	}

	/**
	 * Sets this player character's location.
	 */
	@Override
	public void setLocation(Location location) {
		Location oldLocation = getLocation();
		super.setLocation(location);
		hitbox.setCenter(getColliderCenter());
		double distanceSquared = location.distanceSquared(oldLocation);
		if (distanceSquared > MAX_DISPLACEMENT_WITHOUT_TELEPORT_SQUARED) {
			unhidePlayerNameplate();
			player.teleport(location);
			hidePlayerNameplate();
		}
		updateActionBar();
	}

	/**
	 * Returns the direction that the player is looking in.
	 * 
	 * @return
	 */
	public Vector getLookDirection() {
		return player.getLocation().getDirection();
	}

	public float getYaw() {
		return player.getLocation().getYaw();
	}

	public void setYaw(float yaw) {
		Location location = getLocation();
		location.setYaw(yaw);
		unhidePlayerNameplate();
		player.teleport(location);
		hidePlayerNameplate();
	}

	public float getPitch() {
		return player.getLocation().getPitch();
	}

	public void setPitch(float pitch) {
		Location location = getLocation();
		location.setPitch(pitch);
		unhidePlayerNameplate();
		player.teleport(location);
		hidePlayerNameplate();
	}

	/**
	 * Returns the world that this player character is in.
	 */
	public World getWorld() {
		return player.getWorld();
	}

	/**
	 * Returns the location of this player character's hand. Useful for determining
	 * the start location of spells.
	 */
	public Location getHandLocation() {
		Location handLocation = getLocation().add(0, 1, 0);
		Vector lookDirection = getLookDirection();
		lookDirection.rotateAroundY(-Math.PI / 4.0);
		return handLocation.add(lookDirection.multiply(0.5));
	}

	/**
	 * Returns where this player character will respawn on death.
	 */
	public Location getRespawnLocation() {
		return respawnLocation;
	}

	/**
	 * Sets where this player character will respawn on death.
	 */
	public void setRespawnLocation(Location respawnLocation) {
		if (!this.respawnLocation.equals(respawnLocation)) {
			this.respawnLocation = respawnLocation;
			sendMessage(ChatColor.GRAY + "Respawn point updated");
		}
	}

	/**
	 * Returns the location of the player's target block or the location that is a
	 * distance of maxRange away from the player in their look direction.
	 */
	public Location getTargetLocation(double maxRange) {
		if (active) {
			// add 0.5 to average
			return player.getTargetBlock(null, (int) maxRange).getLocation().add(0.5, 0.5, 0.5);
		} else {
			return getLocation();
		}
	}

	/**
	 * Ensures that this player character's action bar is displaying the right text.
	 */
	private void updateActionBar() {
		// so 0 current health isn't displayed when alive
		int rCurrentHealth = (int) Math.ceil(getCurrentHealth());
		int rMaxHealth = (int) Math.round(getMaxHealth());
		int rCurrentMana = (int) Math.round(currentMana);
		int rMaxMana = (int) Math.round(maxMana);
		Location location = player.getLocation();
		int rX = (int) Math.round(location.getX());
		int rY = (int) Math.round(location.getY());
		int rZ = (int) Math.round(location.getZ());
		String direction = CardinalDirection.forVector(location.getDirection()).toString();
		String text = String.format(
				ChatColor.RED + "HP: %d/%d  " + ChatColor.GREEN + "(%d, %d, %d) %s  " + ChatColor.AQUA + "MP: %d/%d",
				rCurrentHealth, rMaxHealth, rX, rY, rZ, direction, rCurrentMana, rMaxMana);
		ActionBarText bar = new ActionBarText(text);
		bar.apply(player);
	}

	/**
	 * Displays this player character's health display with hearts.
	 */
	private void updateHealthDisplay() {
		double proportion = getCurrentHealth() / getMaxHealth();
		double hearts = proportion * 20;
		hearts = Math.max(hearts, 1);
		player.setHealth(hearts);
	}

	/**
	 * Displays this player character's mana display with the hunger bar.
	 */
	private void updateManaDisplay() {
		double proportion = currentMana / maxMana;
		int foodLevel = (int) Math.ceil(proportion * 20);
		player.setFoodLevel(foodLevel);
	}

	/**
	 * Must unhide nameplate before teleporting player because armor stand
	 * passengers.
	 */
	private void hidePlayerNameplate() {
		// adding an armor stand passenger to a player hides their nameplate
		Location location = getLocation();
		ArmorStand passenger = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		passenger.setVisible(false);
		passenger.setSmall(true);
		passenger.setMarker(true);
		passenger.setInvulnerable(true);
		passenger.setCollidable(false);
		player.addPassenger(passenger);
	}

	/**
	 * Must unhide nameplate before teleporting player because armor stand
	 * passengers.
	 */
	private void unhidePlayerNameplate() {
		List<Entity> passengers = player.getPassengers();
		if (!passengers.isEmpty()) {
			ArmorStand passenger = (ArmorStand) player.getPassengers().get(0);
			player.removePassenger(passenger);
			passenger.remove();
		}
	}

	/**
	 * Give player characters xp in an area.
	 */
	public static void distributeXp(Location location, double radius, int amount) {
		double diameter = radius * 2;
		Collider bounds = new Collider(location, diameter, diameter, diameter) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.giveXp(amount);
				}
			}
		};
		bounds.setActive(true);
		bounds.setActive(false);
	}

	/**
	 * Returns how much total xp this player character has earned.
	 */
	public int getXP() {
		return xp;
	}

	/**
	 * Gives the specified xp to this player character.
	 */
	public void giveXp(int xp) {
		if (xp == 0) {
			return;
		}
		this.xp += xp;
		this.xp = (int) MathUtility.clamp(this.xp, 0, MAX_XP);
		sendMessage(ChatColor.GREEN + "+" + xp + " XP!");
		checkForLevelUp();
		updateXpDisplay();
		updateActionBar();
	}

	/**
	 * Displays xp progress with the xp bar.
	 */
	private void updateXpDisplay() {
		int level = getLevel();
		player.setLevel(level);
		int xpTarget = level == maxLevel() ? 0 : XP_REQS[level - 1];
		int currentLevelXp = getCurrentLevelXp();
		float levelProgress = level == maxLevel() ? 0f : (float) currentLevelXp / xpTarget;
		player.setExp(levelProgress);
	}

	/**
	 * Returns how much xp this player character has gained while at its current
	 * level.
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

	/**
	 * Sees if this player character has enough xp to level up.
	 */
	private void checkForLevelUp() {
		int newLevel = xpToLevel(xp);
		while (newLevel > getLevel()) {
			levelUp();
		}
	}

	/**
	 * Returns what level the specified xp amount corresponds to.
	 */
	public static int xpToLevel(int xp) {
		for (int i = 0; i < XP_REQS.length; i++) {
			int xpValue = XP_REQS[i];
			xp -= xpValue;
			if (xp < 0) {
				return i + 1;
			}
		}
		return maxLevel();
	}

	/**
	 * Returns the max level attainable by a player character.
	 */
	public static int maxLevel() {
		return XP_REQS.length + 1;
	}

	/**
	 * Levels up this player character.
	 */
	private void levelUp() {
		int newLevel = getLevel() + 1;
		setLevel(newLevel);
		setSkillUpgradePoints(skillUpgradePoints + 1);
		PlayerCharacterLevelUpEvent event = new PlayerCharacterLevelUpEvent(this, newLevel);
		EventManager.callEvent(event);
		Noise levelUpNoise = new Noise(Sound.ENTITY_PLAYER_LEVELUP);
		levelUpNoise.play(player);
		sendMessage(ChatColor.GRAY + "Level increased to " + ChatColor.GOLD + newLevel + ChatColor.GRAY + "!");
	}

	/**
	 * Returns how many unused skill upgrade points this player character has.
	 */
	public int getSkillUpgradePoints() {
		return skillUpgradePoints;
	}

	/**
	 * Sets how many unused skill upgrade points this player character has. Note
	 * that players automatically receive skill points when they level up.
	 */
	public void setSkillUpgradePoints(int points) {
		this.skillUpgradePoints = points;
	}

	/**
	 * Returns how much currency this player character has.
	 */
	public int getCurrency() {
		return currency;
	}

	/**
	 * Returns this player character's maximum mana.
	 */
	public double getMaxMana() {
		return maxMana;
	}

	/**
	 * Sets this player character's maximum mana.
	 */
	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
		updateActionBar();
	}

	/**
	 * Sets this player character's current health.
	 */
	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		updateActionBar();
		updateHealthDisplay();
	}

	/**
	 * Sets this player character's max health.
	 */
	@Override
	public void setMaxHealth(double maxHealth) {
		super.setMaxHealth(maxHealth);
		updateActionBar();
		updateHealthDisplay();
	}

	/**
	 * Returns this player character's health regeneration rate in health per
	 * second.
	 */
	public double getHealthRegenRate() {
		return healthRegenRate;
	}

	/**
	 * Sets this player character's health regeneration rate in health per second.
	 */
	public void setHealthRegenRate(double healthRegenRate) {
		this.healthRegenRate = healthRegenRate;
	}

	/**
	 * Returns this player character's current mana.
	 */
	public double getCurrentMana() {
		return currentMana;
	}

	/**
	 * Sets this player character's current mana.
	 */
	public void setCurrentMana(double currentMana) {
		currentMana = MathUtility.clamp(currentMana, 0, maxMana);
		this.currentMana = currentMana;
		updateActionBar();
		updateManaDisplay();
	}

	/**
	 * Returns this player character's mana regeneration rate in mana per second.
	 */
	public double getManaRegenRate() {
		return manaRegenRate;
	}

	/**
	 * Sets this player character's mana regeneration rate in mana per second.
	 */
	public void setManaRegenRate(double manaRegenRate) {
		this.manaRegenRate = manaRegenRate;
	}

	/**
	 * Deals the specified damage to this player character (pre-mitigation).
	 */
	@Override
	public void damage(double amount, Source source) {
		amount = getMitigatedDamage(amount, getProtections());
		super.damage(amount, source);
		player.damage(0);
		HURT_NOISE.play(getLocation());
	}

	/**
	 * Returns how much damage will be done to this player character after
	 * mitigations.
	 */
	private static double getMitigatedDamage(double damage, double protections) {
		if (damage == 0) {
			// prevent divide by 0
			return 0;
		}
		double damageMultiplier = damage / (damage + protections);
		return damage * damageMultiplier;
	}

	/**
	 * Returns the weapon that this player character is holding.
	 */
	public Weapon getWeapon() {
		if (map.isOpen()) {
			return map.getPlayerCharacterWeapon();
		}
		ItemStack itemStack = player.getInventory().getItem(0);
		return (Weapon) Item.forItemStack(itemStack);
	}

	/**
	 * Returns how many protections this player character has from armor.
	 */
	public double getProtections() {
		double protections = 0;
		int level = getLevel();
		ArmorItem head = getHeadArmor();
		if (head != null) {
			if (level >= head.getLevel() && playerClass == head.getPlayerClass()) {
				protections += head.getProtections();
			} else {
				sendMessage(ChatColor.GRAY + "Unable to equip " + head);
			}
		}
		ArmorItem chest = getChestArmor();
		if (chest != null) {
			if (level >= chest.getLevel() && playerClass == chest.getPlayerClass()) {
				protections += chest.getProtections();
			} else {
				sendMessage(ChatColor.GRAY + "Unable to equip " + chest);
			}
		}
		ArmorItem legs = getLegArmor();
		if (legs != null) {
			if (level >= legs.getLevel() && playerClass == legs.getPlayerClass()) {
				protections += legs.getProtections();
			} else {
				sendMessage(ChatColor.GRAY + "Unable to equip " + legs);
			}
		}
		ArmorItem feet = getFeetArmor();
		if (feet != null) {
			if (level >= feet.getLevel() && playerClass == feet.getPlayerClass()) {
				protections += feet.getProtections();
			} else {
				sendMessage(ChatColor.GRAY + "Unable to equip " + feet);
			}
		}
		return protections;
	}

	/**
	 * Returns the item in the head slot of this player character's inventory.
	 */
	public ArmorItem getHeadArmor() {
		return getArmorItem(39);
	}

	/**
	 * Returns the item in the chest slot of this player character's inventory.
	 */
	public ArmorItem getChestArmor() {
		return getArmorItem(38);
	}

	/**
	 * Returns the item in the leg slot of this player character's inventory.
	 */
	public ArmorItem getLegArmor() {
		return getArmorItem(37);
	}

	/**
	 * Returns the item in the feet slot of this player character's inventory.
	 */
	public ArmorItem getFeetArmor() {
		return getArmorItem(36);
	}

	/**
	 * Returns the armor item in the specified inventory slot.
	 */
	private ArmorItem getArmorItem(int inventorySlot) {
		ItemStack itemStack = player.getInventory().getItem(inventorySlot);
		Item item = Item.forItemStack(itemStack);
		if (item instanceof ArmorItem) {
			return (ArmorItem) item;
		}
		return null;
	}

	/**
	 * Returns this player character's quest manager.
	 */
	public PlayerCharacterQuestManager getQuestManager() {
		return questStatusManager;
	}

	/**
	 * Returns this player character's quest log.
	 */
	public QuestLog getQuestLog() {
		return questLog;
	}

	/**
	 * Returns this player character's skill manager.
	 */
	public PlayerCharacterSkillManager getSkillManager() {
		return skillStatusManager;
	}

	/**
	 * Returns whether this player character has the specified tag. Tags are used to
	 * store data that can't otherwise be stored.
	 */
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	/**
	 * Adds the specified tag to this player character. Tags are used to store data
	 * that can't otherwise be stored.
	 */
	public void addTag(String tag) {
		tags.add(tag);
	}

	/**
	 * Removes the specified tag from this player character. Tags are used to store
	 * data that can't otherwise be stored.
	 */
	public void removeTag(String tag) {
		tags.remove(tag);
	}

	/**
	 * Returns all tags added to this player character.
	 */
	public String[] getTags() {
		return tags.toArray(new String[tags.size()]);
	}

	/**
	 * Returns the total play time on this player character in seconds.
	 */
	public double getTotalPlayTime() {
		double currentTime = Clock.getTime();
		return previousPlayTime + currentTime - playSessionStartTime;
	}

	/**
	 * Returns whether this player character is disarmed (unable to use basic
	 * attacks).
	 */
	public boolean isDisarmed() {
		return isDisarmed;
	}

	/**
	 * Prevent this player character from using weapons for the duration specified.
	 */
	public void disarm(double duration) {
		if (undisarmTask != null) {
			undisarmTask.cancel();
		}
		isDisarmed = true;
		undisarmTask = new DelayedTask(duration) {
			@Override
			protected void run() {
				isDisarmed = false;
				undisarmTask = null;
			}
		};
		undisarmTask.schedule();
	}

	/**
	 * Returns whether this player character is silenced (unable to use skills).
	 */
	public boolean isSilenced() {
		return isSilenced;
	}

	/**
	 * Prevent this player character from using skills and consumables for the
	 * duration specified.
	 */
	public void silence(double duration) {
		if (unsilenceTask != null) {
			unsilenceTask.cancel();
		}
		isSilenced = true;
		unsilenceTask = new DelayedTask(duration) {
			@Override
			protected void run() {
				isSilenced = false;
				unsilenceTask = null;
			}
		};
		unsilenceTask.schedule();
	}

	/**
	 * Invoked when this player character dies.
	 */
	@Override
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		setNameplateVisible(false);
		setHealthBarVisible(false);
		disarm(4);
		silence(4);
		TitleText deathMessage = new TitleText(ChatColor.RED + "YOU DIED", null);
		deathMessage.apply(player);
		broadcastMessage(formatName() + ChatColor.GRAY + " died", getLocation(), 25);
		PotionEffect veilEffect = new PotionEffect(PotionEffectType.BLINDNESS, 80, 1);
		PotionEffect invisibiltyEffect = new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1);
		PotionEffect slownessEffect = new PotionEffect(PotionEffectType.SLOW, 40, 5);
		player.addPotionEffect(veilEffect);
		player.addPotionEffect(invisibiltyEffect);
		player.addPotionEffect(slownessEffect);
		DEATH_NOISE.play(player);

		new DelayedTask(2) {
			@Override
			protected void run() {
				sendMessage(ChatColor.GRAY + "Respawning...");
				setLocation(respawnLocation);
				setAlive(true);
				hitbox.setActive(true);
				setNameplateVisible(true);
				setHealthBarVisible(true);
			}
		}.schedule();
	}

	/**
	 * Returns true if the other character is also a player character.
	 */
	@Override
	public boolean isFriendly(Character other) {
		return other instanceof PlayerCharacter;
	}

	/**
	 * Returns where this player character's hitbox should be relative to this
	 * player character's location.
	 */
	private Location getColliderCenter() {
		Location location = getLocation();
		return location.add(0.0, 1.0, 0.0);
	}

	/**
	 * Returns the player associated with this player character.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sends the specified message to this player character.
	 */
	public void sendMessage(String message) {
		player.sendMessage(message);
	}

	/**
	 * Sends the specified message to player characters nearby the location.
	 */
	public static void broadcastMessage(String message, Location location, double radius) {
		double diameter = radius * 2;
		Collider area = new Collider(location, diameter, diameter, diameter);
		area.setActive(true);
		Collider[] contacts = area.getContacts();
		for (Collider contact : contacts) {
			if (contact instanceof PlayerCharacterCollider) {
				PlayerCharacter pc = ((PlayerCharacterCollider) contact).getCharacter();
				pc.sendMessage(message);
			}
		}
		area.setActive(false);
	}

	/**
	 * Gives the specified item to this player character.
	 */
	public void giveItem(Item item) {
		giveItem(item, 1);
	}

	/**
	 * Gives the specified item in the specified amount to this player character.
	 */
	public void giveItem(Item item, int amount) {
		if (amount == 0) {
			return;
		}
		ItemStack itemStack = item.getItemStack();
		itemStack.setAmount(amount);
		Inventory inventory = player.getInventory();
		inventory.addItem(itemStack);
		sendMessage(ChatColor.GRAY + "You received " + (amount > 1 ? amount + " " : "") + item + ChatColor.GRAY + "!");
		PlayerCharacterReceiveItemEvent event = new PlayerCharacterReceiveItemEvent(this, item, amount);
		EventManager.callEvent(event);
	}

	/**
	 * Removes the specified item in the specified amount from this player
	 * character.
	 */
	public void removeItem(Item item, int amount) {
		if (amount == 0) {
			return;
		}
		int removedAmount = 0;
		ItemStack itemStack = item.getItemStack();
		Inventory inventory = player.getInventory();
		ItemStack[] contents = Arrays.copyOf(inventory.getContents(), 42);
		contents[41] = player.getItemOnCursor();
		for (int i = 1; i < contents.length; i++) {
			// skip weapon item stack
			ItemStack content = contents[i];
			if (content == null) {
				continue;
			}
			ItemStack unitContent = content.clone();
			unitContent.setAmount(1);
			if (itemStack.equals(unitContent)) {
				int reduction = Math.min(amount, content.getAmount());
				amount -= reduction;
				removedAmount += reduction;
				content.setAmount(content.getAmount() - reduction);
				if (amount == 0) {
					break;
				}
			}
		}
		if (removedAmount > 0) {
			sendMessage(ChatColor.GRAY + "Removed " + (removedAmount == 1 ? "" : removedAmount + " ") + item);
			PlayerCharacterRemoveItemEvent event = new PlayerCharacterRemoveItemEvent(this, item, removedAmount);
			EventManager.callEvent(event);
		}
	}

	/**
	 * Returns how many of the item the player character is carrying.
	 */
	public int getItemCount(Item item) {
		if (item == null) {
			throw new IllegalArgumentException("null item");
		}
		int count = 0;
		ItemStack target = item.getItemStack();
		Inventory inventory = player.getInventory();
		ItemStack[] contents = Arrays.copyOf(inventory.getContents(), 42);
		contents[41] = player.getItemOnCursor();
		for (ItemStack itemStack : contents) {
			if (itemStack == null) {
				continue;
			}
			ItemStack unitItemStack = itemStack.clone();
			unitItemStack.setAmount(1);
			if (unitItemStack.equals(target)) {
				count += itemStack.getAmount();
			}
		}
		return count;
	}

	/**
	 * Use this to set the song playing to the player.
	 */
	public PlayerCharacterSoundtrackPlayer getSoundTrackPlayer() {
		return soundtrackPlayer;
	}

	public PlayerCharacterMap getMap() {
		return map;
	}

	/**
	 * Returns whether this player character is currently being used by a player.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Returns the party that this player character is currently in, or null if this
	 * player character is not in a party.
	 */
	public Party getParty() {
		return party;
	}

	/**
	 * Only for use within the Party class.
	 */
	public void setParty(Party party) {
		this.party = party;
	}

	/**
	 * This needs to be invoked when the player abandons this player character.
	 */
	public void remove() {
		active = false;
		hitbox.setActive(false);
		setNameplateVisible(false);
		setHealthBarVisible(false);
		unhidePlayerNameplate();
		soundtrackPlayer.setSoundtrack(null);
		map.close();
		motionSyncer.setEnabled(false);
		if (party != null) {
			party.remove(this);
		}
		pcs.remove(this);
		playerMap.remove(player);
		PlayerCharacterRemoveEvent event = new PlayerCharacterRemoveEvent(this);
		EventManager.callEvent(event);
		ActionBarText.clear(player);
		SidebarText.clear(this);
	}

	/**
	 * Handles events pertaining to player characters.
	 */
	private static class PlayerCharacterListener implements Listener {

		/**
		 * Used to ensure that the player does not interact multiple times with one
		 * click due to multiple types of events being fired.
		 */
		private final Set<PlayerCharacter> interactingPlayers = new HashSet<>();

		@EventHandler
		private void onKillPlayerCharacter(CharacterKillEvent event) {
			Character killed = event.getKilled();
			if (killed instanceof PlayerCharacter) {
				Source killer = event.getKiller();
				((PlayerCharacter) killed).sendMessage(ChatColor.GRAY + "Killed by " + killer.getName());
			}
		}

		@EventHandler
		private void onInteract(PlayerInteractEvent event) {
			Action action = event.getAction();
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				handleInteraction(event.getPlayer());
			}
		}

		@EventHandler
		private void onInteract(PlayerInteractEntityEvent event) {
			handleInteraction(event.getPlayer());
		}

		@EventHandler
		private void onInteract(PlayerInteractAtEntityEvent event) {
			handleInteraction(event.getPlayer());
		}

		private void handleInteraction(Player player) {
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null || interactingPlayers.contains(pc)) {
				return;
			}
			Location start = player.getLocation().add(0, 1.5, 0);
			Raycast raycast = new Raycast(start, start.getDirection(), INTERACT_RANGE,
					PlayerCharacterInteractionCollider.class);
			List<RaycastHit> hits = raycast.getHits();
			if (hits.size() > 0) {
				RaycastHit hit = hits.get(0);
				PlayerCharacterInteractionCollider collider = (PlayerCharacterInteractionCollider) hit.getCollider();
				collider.onInteract(pc);
			}
			interactingPlayers.add(pc);
			new DelayedTask(INTERACT_COOLDOWN) {
				@Override
				protected void run() {
					interactingPlayers.remove(pc);
				}
			}.schedule();
		}

		@EventHandler
		private void onSendMessage(AsyncPlayerChatEvent event) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			event.setCancelled(true);
			String message = event.getMessage();
			pc.speak(message, SPEAK_RADIUS);
		}

		@EventHandler
		private void onDamage(EntityDamageEvent event) {
			Entity entity = event.getEntity();
			if (entity.getType() == EntityType.PLAYER) {
				event.setDamage(0);
			}
		}

	}

}
