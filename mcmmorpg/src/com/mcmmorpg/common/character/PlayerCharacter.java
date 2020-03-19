package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterRegisterEvent;
import com.mcmmorpg.common.event.PlayerCharacterRemoveEvent;
import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillData;
import com.mcmmorpg.common.playerClass.PlayerSkillManager;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestLog;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.PlayerSoundtrackPlayer;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ActionBarText;
import com.mcmmorpg.common.ui.SidebarText;
import com.mcmmorpg.common.ui.TitleMessage;
import com.mcmmorpg.common.utils.MathUtils;

public final class PlayerCharacter extends AbstractCharacter {

	/**
	 * The value at index 0 is the amount of xp it takes to level up from level 1 to
	 * level 2. The value at index 1 is the xp it takes to level up from level 2 to
	 * level 3.
	 */
	private static final int[] XP_REQS = { 100, 150, 225, 325, 450, 600, 775, 975, 1200, 1450, 1725, 2025, 2350, 2700,
			3075, 3475, 3900, 4350, 4825 };
	private static final int MAX_XP = getMaxXp();
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_WITHER_SPAWN);
	private static final double MAX_DISTANCE_WITHOUT_PLAYER_TELEPORT = 5.0;
	private static final double INTERACT_RANGE = 4;

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
	private final List<String> tags;
	private final PlayerSoundtrackPlayer soundtrackPlayer;
	private CharacterCollider collider;
	private final MovementSyncer movementSyncer;
	private boolean isDisarmed;
	private transient boolean isSilenced;

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
			ItemStack[] inventoryContents, String[] tags) {
		super(ChatColor.GREEN + player.getName(), xpToLevel(xp), location);
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
		this.targetQuest = targetQuest;
		this.questStatusManager = new PlayerQuestManager(completedQuests, questData);
		this.skillStatusManager = new PlayerSkillManager(this, skillData);
		this.skillStatusManager.init();
		player.getInventory().setContents(inventoryContents);
		this.tags = new ArrayList<>(Arrays.asList(tags));
		soundtrackPlayer = new PlayerSoundtrackPlayer(player);
		this.collider = new PlayerCharacterCollider(this);
		player.teleport(getLocation());

		this.movementSyncer = new MovementSyncer(this, player, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		movementSyncer.setEnabled(true);
		collider.setActive(true);

		active = true;
		setAlive(true);
		super.setCurrentHealth(currentHealth);
		pcs.add(this);
		playerMap.put(player, this);

		updateActionBar();
		updateQuestDisplay();
		updateXpDisplay();
		updateHealthDisplay();
		updateManaDisplay();

		if (fresh) {
			setSkillUpgradePoints(1);
			PlayerCharacterLevelUpEvent event = new PlayerCharacterLevelUpEvent(this, 1);
			EventManager.callEvent(event);
		}
		isDisarmed = false;
		isSilenced = false;
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
		String[] tags = saveData.getTags();

		PlayerCharacter pc = new PlayerCharacter(player, fresh, playerClass, zone, location, respawnLocation, xp,
				skillUpgradePoints, currency, maxHealth, currentHealth, healthRegenRate, maxMana, currentMana,
				manaRegenRate, targetQuest, saveData.getCompletedQuests(), questStatuses, skillStatuses,
				inventoryContents, tags);
		PlayerCharacterRegisterEvent event = new PlayerCharacterRegisterEvent(pc);
		EventManager.callEvent(event);
		return pc;
	}

	public static PlayerCharacter forPlayer(Player player) {
		return playerMap.get(player);
	}

	/**
	 * Returns true if there is a player within the specified x, y, and z distances
	 * from the specified location. False otherwise.
	 */
	public static boolean playerCharacterIsNearby(Location location, double radius) {
		World world = location.getWorld();
		Collection<Entity> nearbyEntities = world.getNearbyEntities(location, radius, radius, radius);
		for (Entity nearbyEntity : nearbyEntities) {
			if (nearbyEntity.getType().equals(EntityType.PLAYER)) {
				if (playerMap.containsKey(nearbyEntity)) {
					return true;
				}
			}
		}
		return false;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
		sendMessage(ChatColor.GRAY + "Entering " + zone);
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
		sendMessage(ChatColor.GRAY + "Respawn point updated");
	}

	/**
	 * Returns the location of the player's target block or the location that is a
	 * distance of maxRange away from the player in their look direction.
	 */
	public Location getTargetLocation(double maxRange) {
		// add 0.5 to average
		return player.getTargetBlock(null, (int) maxRange).getLocation().add(0.5, 0.5, 0.5);
	}

	private void updateActionBar() {
		int rCurrentHealth = (int) Math.round(getCurrentHealth());
		int rMaxHealth = (int) Math.round(getMaxHealth());
		int rCurrentMana = (int) Math.round(currentMana);
		int rMaxMana = (int) Math.round(maxMana);
		int currentLevelXp = getCurrentLevelXp();
		int level = getLevel();
		int targetXp = level == maxLevel() ? 0 : XP_REQS[level - 1];
		String text = String.format(
				ChatColor.RED + "HP: %d/%d    " + ChatColor.AQUA + "MP: %d/%d    " + ChatColor.GREEN + "XP: %d/%d",
				rCurrentHealth, rMaxHealth, rCurrentMana, rMaxMana, currentLevelXp, targetXp);
		ActionBarText bar = new ActionBarText(text);
		bar.apply(player);
	}

	private void updateHealthDisplay() {
		double proportion = getCurrentHealth() / getMaxHealth();
		double hearts = proportion * 20;
		hearts = Math.max(hearts, 1);
		player.setHealth(hearts);
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
		sendMessage(ChatColor.GREEN + "+" + xp + " XP!");
		checkForLevelUp();
		updateXpDisplay();
		updateActionBar();
	}

	private void updateXpDisplay() {
		int level = getLevel();
		player.setLevel(level);
		int xpTarget = level == maxLevel() ? 0 : XP_REQS[level - 1];
		int currentLevelXp = getCurrentLevelXp();
		float levelProgress = level == maxLevel() ? 0f : (float) currentLevelXp / xpTarget;
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

	public static int maxLevel() {
		return XP_REQS.length + 1;
	}

	private void levelUp() {
		int newLevel = getLevel() + 1;
		setLevel(newLevel);
		setSkillUpgradePoints(skillUpgradePoints + 1);
		PlayerCharacterLevelUpEvent event = new PlayerCharacterLevelUpEvent(this, newLevel);
		EventManager.callEvent(event);
		Noise levelUpNoise = new Noise(Sound.ENTITY_PLAYER_LEVELUP);
		levelUpNoise.play(player);
		sendMessage(ChatColor.GRAY + "Level increased to " + ChatColor.GOLD + newLevel + ChatColor.GRAY + "!");
		sendMessage(ChatColor.GRAY + "+1 skill point!");
	}

	public int getSkillUpgradePoints() {
		return skillUpgradePoints;
	}

	/**
	 * Note that players automatically receive skill points when they level up.
	 */
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
		player.damage(0.01);
	}

	private static double getMitigatedDamage(double damage, double protections) {
		double damageMultiplier = damage / (damage + protections);
		return damage * damageMultiplier;
	}

	public Weapon getWeapon() {
		ItemStack itemStack = player.getInventory().getItem(0);
		return (Weapon) Item.forItemStack(itemStack);
	}

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

	public ArmorItem getHeadArmor() {
		return getArmorItem(39);
	}

	public ArmorItem getChestArmor() {
		return getArmorItem(38);
	}

	public ArmorItem getLegArmor() {
		return getArmorItem(37);
	}

	public ArmorItem getFeetArmor() {
		return getArmorItem(36);
	}

	private ArmorItem getArmorItem(int inventorySlot) {
		ItemStack itemStack = player.getInventory().getItem(inventorySlot);
		Item item = Item.forItemStack(itemStack);
		if (item instanceof ArmorItem) {
			return (ArmorItem) item;
		}
		return null;
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

	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public void removeTag(String tag) {
		tags.remove(tag);
	}

	public String[] getTags() {
		return tags.toArray(new String[tags.size()]);
	}

	public boolean isDisarmed() {
		return isDisarmed;
	}

	/**
	 * Prevent this player character from using weapons for the duration specified.
	 */
	public void disarm(double duration) {
		isDisarmed = true;
		new DelayedTask(duration) {
			@Override
			protected void run() {
				isDisarmed = false;
			}
		}.schedule();
	}

	public boolean isSilenced() {
		return isSilenced;
	}

	/**
	 * Prevent this player character from using skills and consumables for the
	 * duration specified.
	 */
	public void silence(double duration) {
		isSilenced = true;
		new DelayedTask(duration) {
			@Override
			protected void run() {
				isSilenced = false;
			}
		}.schedule();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		disarm(4);
		silence(4);
		TitleMessage deathMessage = new TitleMessage(ChatColor.RED + "YOU DIED", "");
		deathMessage.sendTo(player);
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
				player.teleport(respawnLocation);
				setAlive(true);
			}
		}.schedule();
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return other instanceof PlayerCharacter;
	}

	public void updateQuestDisplay() {
		String lines = "";
		List<Quest> inProgressQuests = Quest.getInProgressQuests(this);
		for (Quest quest : inProgressQuests) {
			lines += ChatColor.YELLOW + quest.getName() + "\n" + quest.getQuestLogLines(this);
		}
		SidebarText questDisplay = new SidebarText(ChatColor.YELLOW + "Quests", lines);
		questDisplay.apply(player);
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

	public void giveItem(Item item) {
		giveItem(item, 1);
	}

	public void giveItem(Item item, int amount) {
		ItemStack itemStack = item.getItemStack();
		itemStack.setAmount(amount);
		Inventory inventory = player.getInventory();
		inventory.addItem(itemStack);
	}

	public void removeItem(Item item, int amount) {
		ItemStack itemStack = item.getItemStack();
		Inventory inventory = player.getInventory();
		ItemStack[] contents = inventory.getContents();
		for (ItemStack content : contents) {
			ItemStack unitContent = content.clone();
			unitContent.setAmount(1);
			if (itemStack.equals(unitContent)) {
				int reduction = Math.min(amount, content.getAmount());
				amount -= reduction;
				content.setAmount(content.getAmount() - reduction);
				if (amount == 0) {
					break;
				}
			}
		}
	}

	/**
	 * Returns how many of the item the player is carrying in their inventory.
	 */
	public int getItemCount(Item item) {
		if (item == null) {
			throw new IllegalArgumentException("null item");
		}
		int count = 0;
		ItemStack target = item.getItemStack();
		Inventory inventory = player.getInventory();
		ItemStack[] contents = inventory.getContents();
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
	public void remove() {
		active = false;
		collider.setActive(false);
		movementSyncer.setEnabled(false);
		pcs.remove(this);
		playerMap.remove(player);
		PlayerCharacterRemoveEvent event = new PlayerCharacterRemoveEvent(this);
		EventManager.callEvent(event);
		ActionBarText.clear(player);
		SidebarText.clear(player);
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
			Location start = pc.getLocation().add(0, 1.5, 0);
			Ray ray = new Ray(start, start.getDirection(), INTERACT_RANGE);
			Raycast raycast = new Raycast(ray, PlayerCharacterInteractionCollider.class);
			Collider[] hits = raycast.getHits();
			for (Collider hit : hits) {
				PlayerCharacterInteractionCollider collider = (PlayerCharacterInteractionCollider) hit;
				collider.onInteract(pc);
			}
			interactingPlayers.add(pc);
			new DelayedTask(0.1) {
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
			pc.say(message);
		}

		@EventHandler
		private void onDamage(EntityDamageEvent event) {
			// Screw it. Let's just put it here for all entities.
			event.setDamage(0);
		}

		@EventHandler
		private void onCombust(EntityCombustEvent event) {
			event.setCancelled(true);
		}
	}

}
