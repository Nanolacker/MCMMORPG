package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.CharacterKillEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class TrainingDummy extends NonPlayerCharacter {

	private static final Noise DAMAGE_NOISE = new Noise(Sound.BLOCK_IRON_TRAPDOOR_CLOSE);
	private static final Noise DESTROY_NOISE = new Noise(Sound.BLOCK_FENCE_GATE_CLOSE);

	private ArmorStand entity;
	private CharacterCollider hitbox;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onDummyKill(CharacterKillEvent event) {
				AbstractCharacter killed = event.getKilled();
				if (killed instanceof TrainingDummy) {
					Source source = event.getKiller();
					if (source instanceof PlayerCharacter) {
						PlayerCharacter pc = (PlayerCharacter) source;
						Quest.forName("Tutorial Part 2 (Fighter)").getObjective(0).addProgress(pc, 1);
						Quest.forName("Tutorial Part 3 (Fighter)").getObjective(1).addProgress(pc, 1);
						Quest.forName("Tutorial Part 2 (Mage)").getObjective(0).addProgress(pc, 1);
						Quest.forName("Tutorial Part 3 (Mage)").getObjective(1).addProgress(pc, 1);
					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public TrainingDummy(Location location) {
		super(ChatColor.RED + "Training Dummy", 1, location);
		setMaxHealth(20);
		hitbox = new CharacterCollider(this, location.clone().add(0, 1, 0), 1, 2, 1);
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		hitbox.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
		hitbox.setActive(false);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		DAMAGE_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		DESTROY_NOISE.play(getLocation());
		entity.remove();
		hitbox.setActive(false);
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.grantXp(3);
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
		DelayedTask respawnTask = new DelayedTask(10) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawnTask.schedule();
	}

}
