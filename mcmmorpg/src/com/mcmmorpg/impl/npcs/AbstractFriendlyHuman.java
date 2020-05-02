package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.HumanEntity;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;

public abstract class AbstractFriendlyHuman extends NonPlayerCharacter {

	private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE,
			1);

	protected final HumanEntity entity;
	protected final Location spawnLocation;
	protected final PlayerCharacterInteractionCollider interactionCollider;
	protected final MovementSyncer aiSyncer;
	protected Cow ai;

	protected AbstractFriendlyHuman(String name, int level, Location spawnLocation, String textureData,
			String textureSignature) {
		super(name, level, spawnLocation);
		this.entity = new HumanEntity(spawnLocation, textureData, textureSignature);
		this.spawnLocation = spawnLocation;
		this.interactionCollider = new PlayerCharacterInteractionCollider(spawnLocation, 1, 2, 1) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				AbstractFriendlyHuman.this.onInteract(pc);
			}
		};
		this.aiSyncer = new MovementSyncer(this, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		interactionCollider.setActive(true);
		entity.setVisible(true);
		ai = (Cow) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.COW);
		ai.addPotionEffect(INVISIBILITY);
		ai.setSilent(true);
		ai.setCollidable(false);
		ai.setInvulnerable(true);
		ai.eject();
		Entity vehicle = ai.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		ai.setAdult();
		ai.getEquipment().clear();
		ai.setRemoveWhenFarAway(false);
		aiSyncer.setEntity(ai);
		aiSyncer.setEnabled(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		interactionCollider.setActive(false);
		entity.setVisible(false);
		aiSyncer.setEnabled(false);
		ai.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		interactionCollider.setCenter(location.clone().add(0, 1, 0));
		entity.setLocation(location);
	}

	protected abstract void onInteract(PlayerCharacter pc);

}