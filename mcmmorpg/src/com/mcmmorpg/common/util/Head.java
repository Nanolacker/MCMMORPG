package com.mcmmorpg.common.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Head {

	private static final double OFFSET_Y = -0.7125;

	private final String skinName;
	private Location location;
	private ArmorStand entity;

	public Head(String skinName, Location location) {
		this.skinName = skinName;
		this.location = location.clone();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location.clone();
		if (entity != null) {
			Location entityLocation = getEntityLocation(location);
			entity.teleport(entityLocation);
		}
	}

	public void setVisible(boolean visible) {
		if (visible) {
			spawn();
		}
	}

	@SuppressWarnings("deprecation")
	private void spawn() {
		Location entityLocation = getEntityLocation(location);

		entity = (ArmorStand) entityLocation.getWorld().spawnEntity(entityLocation, EntityType.ARMOR_STAND);
		entity.setPersistent(false);
		entity.setGravity(false);
		entity.setCollidable(false);
		entity.setVisible(false);
		entity.setSmall(true);
		entity.setArms(false);
		entity.setMarker(true);

		ItemStack headItemStack = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta) headItemStack.getItemMeta();
		itemMeta.setOwner(skinName);
		headItemStack.setItemMeta(itemMeta);
		entity.getEquipment().setHelmet(headItemStack);
	}

	private static Location getEntityLocation(Location headLocation) {
		return headLocation.clone().add(0.0, OFFSET_Y, 0.0);
	}

}
