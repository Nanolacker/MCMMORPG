package com.mcmmorpg.common.character;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.event.CharacterDeathEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.utils.MathUtils;

/**
 * Represents an in-game character, which could be a player character or a
 * non-player character. Each character has, at the minimum, basic attributes
 * that include a name, level, location, alive flag, current health, max health,
 * and nameplate. This class should be extended to create custom characters.
 * Methods in subclasses which override methods in this class must invoke super.
 */
public abstract class AbstractCharacter {

	private String name;
	private int level;
	private Location location;
	private boolean alive;
	private double currentHealth;
	private double maxHealth;
	private final TextPanel nameplate;

	/**
	 * Constructs a character initialized with max health. By default, the character
	 * will not be alive.
	 */
	protected AbstractCharacter(String name, int level, Location location) {
		this.name = name;
		this.level = level;
		this.location = location;
		alive = false;
		currentHealth = 0;
		maxHealth = 1;
		Location nameplateLocation = getNameplateLocation();
		nameplate = new TextPanel(nameplateLocation);
		nameplate.setCharactersPerLine(25);
		updateNameplateText();
	}

	/**
	 * Returns the name of this character as displayed by its nameplate.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this character and updates its nameplate text. Additional
	 * functionality may be specified in subclasses. Overriding methods must invoke
	 * super.
	 */
	@OverridingMethodsMustInvokeSuper
	public void setName(String name) {
		this.name = name;
		updateNameplateText();
	}

	/**
	 * Returns the level of this character, as displayed by its nameplate.
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * Sets the level of this character and updates its nameplate text. Additional
	 * functionality may be specified in subclasses. Overriding methods must invoke
	 * super.
	 */
	@OverridingMethodsMustInvokeSuper
	public void setLevel(int level) {
		this.level = level;
		updateNameplateText();
	}

	/**
	 * Returns a clone of this character's location. Note that, since the location
	 * is cloned, it cannot be modified externally without the use of setLocation().
	 */
	public final Location getLocation() {
		return location.clone();
	}

	/**
	 * Sets the location of this character and updates its nameplate location. The
	 * location argument is internally cloned so that this characters location
	 * cannot be modified externally. Additional functionality may be specified in
	 * subclasses. Overriding methods must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	public void setLocation(Location location) {
		// clone for safety
		this.location = location.clone();
		Location nameplateLocation = getNameplateLocation();
		nameplate.setLocation(nameplateLocation);
	}

	/**
	 * Returns whether this character is alive.
	 */
	public final boolean isAlive() {
		return alive;
	}

	/**
	 * Sets whether this character is alive. This must be called after instantiating
	 * this character to animate it. If alive == true and this character is not
	 * alive, this character's current health will be set to its max health and
	 * onLive() will be called. If alive == false and this character is alive, this
	 * character's current health will be set to 0 and onDeath() will be called.
	 * Otherwise, nothing happens.
	 */
	public final void setAlive(boolean alive) {
		boolean wasAlive = this.alive;
		this.alive = alive;
		if (wasAlive && !alive) {
			this.currentHealth = 0.0;
			onDeath();
		} else if (!wasAlive && alive) {
			this.currentHealth = maxHealth;
			onLive();
		}
		updateNameplateText();
	}

	/**
	 * Returns the current health of this character.
	 */
	public final double getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * Sets the current health of this character. The health is clamped to [0, max
	 * health]. If the health is 0, this character is considered dead. In such a
	 * case, onDeath() will be called this character will be considered not alive.
	 * This character's nameplate will be updated to display the updated health.
	 * Additional functionality may be specified in subclasses. Overriding methods
	 * must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	public void setCurrentHealth(double currentHealth) {
		currentHealth = MathUtils.clamp(currentHealth, 0.0, maxHealth);
		this.currentHealth = currentHealth;
		if (currentHealth == 0.0) {
			setAlive(false);
		}
		updateNameplateText();
	}

	public void damage(double amount, HealthSource source) {
		// Don't damage negative amount.
		amount = Math.max(amount, 0);
		double newHealth = currentHealth - amount;
		setCurrentHealth(newHealth);
		EventManager.callEvent(new CharacterDamageCharacterEvent(this, source, amount);
		if (newHealth <= 0) {
			EventManager.callEvent(new CharacterKillCharacterEvent (this,source,amount));
		}
	}

	private static double getMitigatedDamage(double damage, double protections) {
		double damageMultiplier = damage / (damage + protections);
		return damage * damageMultiplier;
	}

	public void heal(double amount, HealthSource source) {
		// Don't heal negative amount.
		amount = Math.max(amount, 0);
		setCurrentHealth(currentHealth + amount);
		ChcaracterHealCharacterEvent event = new ChcaracterHealCharacterEvent(this, source, amount);
	}

	/**
	 * Returns this character's max health.
	 */
	public final double getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Sets this character's max health and updates its nameplate text. Additional
	 * functionality may be specified in subclasses. Overriding methods must invoke
	 * super.
	 */
	@OverridingMethodsMustInvokeSuper
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
		updateNameplateText();
	}

	/**
	 * Invoked when setAlive(true) is called. Additional functionality may be
	 * specified in subclasses. Overriding methods must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void onLive() {
	}

	/**
	 * Invoked when this character dies, either by calling setAlive(false) or
	 * setCurrentHealth(0). By default, this will hide this character's nameplate.
	 * Additional functionality may be specified in subclasses. Overriding methods
	 * must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void onDeath() {
		CharacterDeathEvent event = new CharacterDeathEvent(this);
		EventManager.callEvent(event);
		setNameplateVisible(false);
	}

	private final String nameplateText() {
		int numBars = 20;
		StringBuilder text = new StringBuilder();
		text.append(ChatColor.WHITE + "[" + ChatColor.GOLD + "Lv. " + level + ChatColor.WHITE + "] " + ChatColor.RESET
				+ name + '\n');
		text.append(ChatColor.WHITE + "[");
		double currentToMaxHealthRatio = currentHealth / maxHealth;
		int numRedBars = (int) (numBars * currentToMaxHealthRatio);
		text.append(ChatColor.RED.toString());
		for (int i = 0; i < numRedBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.GRAY.toString());
		for (int i = numRedBars; i < numBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.WHITE + "]");
		return text.toString();
	}

	private final void updateNameplateText() {
		String nameplateText = nameplateText();
		nameplate.setText(nameplateText);
	}

	/**
	 * Sets whether this character's nameplate is visible.
	 */
	public final void setNameplateVisible(boolean visible) {
		nameplate.setVisible(visible);
	}

	/**
	 * This is used to ensure this character's nameplate is always positioned
	 * correctly. Specify in subclasses where this character's nameplate should be
	 * located at any time. The location should usually be relative to this
	 * character's location.
	 */
	protected Location getNameplateLocation() {
		// assume humanoid
		return getLocation().add(0, 2, 0);
	}

	/**
	 * Returns whether this character is friendly toward the other specified
	 * character. Override in subclasses to provide specific behavior. Returns false
	 * by default.
	 */
	public boolean isFriendly(AbstractCharacter other) {
		return false;
	}

}
