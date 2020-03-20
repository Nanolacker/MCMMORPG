package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.impl.Worlds;

public class CrestfordSewersListener implements Listener {

	private final Source sewage;

	public CrestfordSewersListener() {
		Collider innerBounds = new Collider(new Location(Worlds.ELADRADOR, -475, 67, -135), 5, 5, 5);
		innerBounds.setActive(true);

		sewage = new Source() {
			@Override
			public String getName() {
				return ChatColor.RED + "Sewage";
			}
		};

		new RepeatingTask(0.5) {
			@Override
			protected void run() {
				Collider[] colliders = innerBounds.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof PlayerCharacterCollider) {
						PlayerCharacter pc = ((PlayerCharacterCollider) collider).getCharacter();
						Location location = pc.getLocation();
						World world = location.getWorld();
						Location floorLocation = location.subtract(0, 1, 0);
						Block floor = world.getBlockAt(floorLocation);
						if (floor.getType() == Material.GLASS) {
							pc.damage(2, sewage);
						}
					}
				}
			}
		}.schedule();
	}

}
