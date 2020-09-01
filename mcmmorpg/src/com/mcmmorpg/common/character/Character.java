package com.mcmmorpg.common.character;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.event.CharacterDamageEvent;
import com.mcmmorpg.common.event.CharacterDeathEvent;
import com.mcmmorpg.common.event.CharacterHealEvent;
import com.mcmmorpg.common.event.CharacterKillEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.util.MathUtility;

/**
 * Represents an in-game character, which could be a player character or a
 * non-player character. Each character has, at the minimum, basic attributes
 * that include a name, level, location, alive flag, current health, max health,
 * and height. This class should be extended to create custom characters.
 * Methods in subclasses which override methods in this class must invoke super.
 */
public abstract class Character implements Source {

	private String name;
	private int level;
	private Location location;
	private boolean alive;
	private double currentHealth;
	private double maxHealth;
	private double height;
	private final TextPanel nameplate;
	private final TextPanel healthBar;

	/**
	 * Constructs a character initialized with max health. By default, the character
	 * will not be alive.
	 */
	protected Character(String name, int level, Location location) {
		this.name = name;
		this.level = level;
		this.location = location;
		this.alive = false;
		this.currentHealth = 0.0;
		this.maxHealth = 1.0;
		// Assume humanoid height.
		this.height = 2.0;
		Location nameplateLocation = getNameplateLocation();
		this.nameplate = new TextPanel(nameplateLocation, name);
		this.nameplate.setLineLength(25);
		Location healthBarLocation = getHealthBarLocation();
		this.healthBar = new TextPanel(healthBarLocation);
		this.healthBar.setLineLength(25);
		updateNameplateText();
		updateHealthBarText();
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
		updateNameplateLocation();
		updateHealthBarLocation();
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
			onLife();
		}
		updateHealthBarText();
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
		currentHealth = MathUtility.clamp(currentHealth, 0.0, maxHealth);
		this.currentHealth = currentHealth;
		if (currentHealth == 0.0) {
			setAlive(false);
		}
		updateHealthBarText();
	}

	/**
	 * Deals damage to this character from the specified source.
	 */
	@OverridingMethodsMustInvokeSuper
	public void damage(double amount, Source source) {
		if (!alive) {
			return;
		}
		// Don't damage negative amount.
		amount = Math.max(amount, 0.0);
		double newHealth = currentHealth - amount;
		setCurrentHealth(newHealth);
		EventManager.callEvent(new CharacterDamageEvent(this, amount, source));
		if (newHealth <= 0) {
			EventManager.callEvent(new CharacterKillEvent(this, source));
		}
	}

	/**
	 * Heals this character from the specified source.
	 */
	@OverridingMethodsMustInvokeSuper
	public void heal(double amount, Source source) {
		// Don't heal negative amount.
		amount = Math.max(amount, 0.0);
		setCurrentHealth(currentHealth + amount);
		CharacterHealEvent event = new CharacterHealEvent(this, amount, source);
		EventManager.callEvent(event);
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
		updateHealthBarText();
	}

	/**
	 * Invoked when setAlive(true) is called. Additional functionality may be
	 * specified in subclasses. Overriding methods must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void onLife() {
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
		setHealthBarVisible(false);
	}

	/**
	 * Returns how tall this character is.
	 */
	public final double getHeight() {
		return height;
	}

	/**
	 * Sets how tall this character is.
	 */
	public final void setHeight(double height) {
		this.height = height;
		updateNameplateLocation();
		updateHealthBarLocation();
	}

	/**
	 * Determines the text to be displayed on this character's nameplate.
	 */
	private final String nameplateText() {
		return ChatColor.GRAY + "[" + ChatColor.GOLD + "Lv. " + level + ChatColor.GRAY + "] " + ChatColor.RESET + name;

	}

	/**
	 * Determines the text to be displayed on this character's health bar.
	 */
	private final String healthBarText() {
		int numBars = 20;
		StringBuilder text = new StringBuilder();
		text.append(ChatColor.GRAY + "[");
		double currentToMaxHealthRatio = currentHealth / maxHealth;
		int numRedBars = (int) Math.ceil(numBars * currentToMaxHealthRatio);
		text.append(ChatColor.RED.toString());
		for (int i = 0; i < numRedBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.GRAY.toString());
		for (int i = numRedBars; i < numBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.GRAY + "]");
		return text.toString();
	}

	/**
	 * Uses this character's height to determine the placement of its nameplate.
	 */
	private final Location getNameplateLocation() {
		return getLocation().add(0, height, 0);
	}

	/**
	 * Uses this character's height to determine the placement of its health bar.
	 */
	private final Location getHealthBarLocation() {
		return getLocation().add(0, height - 0.25, 0);
	}

	/**
	 * Ensures that this character's nameplate is positioned correctly.
	 */
	private final void updateNameplateLocation() {
		Location nameplateLocation = getNameplateLocation();
		nameplate.setLocation(nameplateLocation);
	}

	/**
	 * Ensures that this character's health bar is positioned correctly.
	 */
	private final void updateHealthBarLocation() {
		Location healthBarLocation = getHealthBarLocation();
		healthBar.setLocation(healthBarLocation);
	}

	/**
	 * Ensures that this character's nameplate is displaying text correctly.
	 */
	private final void updateNameplateText() {
		String nameplateText = nameplateText();
		nameplate.setText(nameplateText);
	}

	/**
	 * Ensures that this character's health bar is displaying text correctly.
	 */
	private final void updateHealthBarText() {
		String healthBarText = healthBarText();
		healthBar.setText(healthBarText);
	}

	/**
	 * Sets whether this character's nameplate is visible.
	 */
	public final void setNameplateVisible(boolean visible) {
		nameplate.setVisible(visible);
	}

	/**
	 * Sets whether this character's health bar is visible.
	 */
	public final void setHealthBarVisible(boolean visible) {
		healthBar.setVisible(visible);
	}

	/**
	 * Returns whether this character is friendly toward the other specified
	 * character. Override in subclasses to provide specific behavior. Returns false
	 * by default.
	 */
	public boolean isFriendly(Character other) {
		return false;
	}

	/**
	 * Returns this character's name formatted with brackets.
	 */
	public final String formatName() {
		return ChatColor.GRAY + "[" + ChatColor.RESET + name + ChatColor.GRAY + "]" + ChatColor.RESET;
	}

	/**
	 * Formats dialogue to be said by this character.
	 */
	public final String formatDialogue(String dialogue) {
		if (dialogue == null) {
			return null;
		} else {
			return formatName() + ChatColor.GRAY + ": " + ChatColor.RESET + dialogue;
		}
	}

	/**
	 * Formats dialogue to be said by this character.
	 */
	public final String[] formatDialogue(String[] dialogue) {
		String[] formattedDialogue = new String[dialogue.length];
		for (int i = 0; i < dialogue.length; i++) {
			formattedDialogue[i] = formatDialogue(dialogue[i]);
		}
		return formattedDialogue;
	}

	/**
	 * Speaks to the specified recipient.
	 */
	@OverridingMethodsMustInvokeSuper
	public void speak(String dialogue, PlayerCharacter recipient) {
		String formattedDialogue = formatDialogue(dialogue);
		recipient.sendMessage(formattedDialogue);
	}

	/**
	 * Speaks to those in an area around this character.
	 */
	@OverridingMethodsMustInvokeSuper
	public void speak(String dialogue, double radius) {
		String formattedDialogue = formatDialogue(dialogue);
		PlayerCharacter.broadcastMessage(formattedDialogue, getLocation(), radius);
	}

}
