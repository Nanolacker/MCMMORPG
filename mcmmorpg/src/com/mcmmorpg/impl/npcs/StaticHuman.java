package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.NPCHuman;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;

/**
 * Not intended to be damaged or killed.
 */
public abstract class StaticHuman extends NonPlayerCharacter {

	private final NPCHuman human;
	private final PlayerCharacterInteractionCollider interactionBox;

	protected StaticHuman(String name, int level, Location location, String textureData, String textureSignature) {
		super(name, level, location);
		human = new NPCHuman("", location, textureData, textureSignature);
		interactionBox = new PlayerCharacterInteractionCollider(location.clone().add(0, 1, 0), 1, 2, 1) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				StaticHuman.this.onInteract(pc);
			}
		};
	}

	protected NPCHuman getHuman() {
		return human;
	}

	@Override
	protected void spawn() {
		super.spawn();
		human.setVisible(true);
		interactionBox.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		human.setVisible(false);
		interactionBox.setActive(false);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		human.setLocation(location);
		interactionBox.setCenter(location.clone().add(0, 1, 0));
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return true;
	}

	protected abstract void onInteract(PlayerCharacter pc);

}
