package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

/**
 * A human entity with equipment.
 */
public class HumanEntity {

	private static final double RENDER_PERIOD = 1.0;
	private static final double RENDER_RADIUS = 35.0;

	private static final List<HumanEntity> humanEntities = new ArrayList<>();

	private Location location;
	private final EntityPlayer entityPlayer;
	private boolean visible;
	private final List<Player> viewers;
	private ArmorStand equipment;
	private ItemStack mainHand, offHand, helmet, chestplate, leggings, boots;

	static {
		RepeatingTask renderer = new RepeatingTask(RENDER_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < humanEntities.size(); i++) {
					HumanEntity humanEntity = humanEntities.get(i);
					humanEntity.render();
				}
			}
		};
		renderer.schedule();
	}

	/**
	 * Creates a new human entity with the skin determined by the specified texture
	 * data and texture signature.
	 */
	public HumanEntity(Location location, String textureData, String textureSignature) {
		this.location = location;
		World world = location.getWorld();
		MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldServer = ((CraftWorld) world).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
		gameProfile.getProperties().put("textures", new Property("textures", textureData, textureSignature));
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
		this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
		viewers = new ArrayList<>();
		setVisible(false);
		humanEntities.add(this);
	}

	/**
	 * Displays this human entity to nearby players.
	 */
	private void render() {
		World world = location.getWorld();
		for (int i = 0; i < viewers.size(); i++) {
			Player player = viewers.get(i);
			World playerWorld = player.getWorld();
			if (playerWorld != world) {
				viewers.remove(player);
			}
		}
		ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) world.getNearbyEntities(location, RENDER_RADIUS,
				RENDER_RADIUS, RENDER_RADIUS);
		for (Entity entity : nearbyEntities) {
			if (entity instanceof Player) {
				show((Player) entity);
			}
		}
	}

	/**
	 * Reveals this human entity to the specified player via packets.
	 */
	private void show(Player player) {
		if (viewers.contains(player)) {
			return;
		}
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);

		playerConnection.sendPacket(addPlayerPacket);
		playerConnection.sendPacket(spawnPacket);
		viewers.add(player);
	}

	/**
	 * Returns the location of this human entity.
	 */
	public Location getLocation() {
		return location.clone();
	}

	/**
	 * Sets the location of this human entity.
	 */
	public void setLocation(Location location) {
		this.location = location;
		if (visible) {
			setLocation0(location);
			equipment.teleport(location);
		}
	}

	/**
	 * Updates the location of this human entity to viewers via packets.
	 */
	private void setLocation0(Location location) {
		entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		for (int i = 0; i < viewers.size(); i++) {
			Player player = viewers.get(i);
			PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
			PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityPlayer);
			// Convert from Bukkit to approximate NMS yaw.
			PacketPlayOutEntityHeadRotation headRotatePacket = new PacketPlayOutEntityHeadRotation(entityPlayer,
					(byte) (0.7 * location.getYaw()));
			playerConnection.sendPacket(teleportPacket);
			playerConnection.sendPacket(headRotatePacket);
		}
	}

	/**
	 * Returns whether players can see this human entity.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether players can see this human entity.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			setLocation0(location);
			equipment = (ArmorStand) BukkitUtility.spawnNonpersistentEntity(location, EntityType.ARMOR_STAND);
			equipment.setVisible(false);
			equipment.setRemoveWhenFarAway(false);
			EntityEquipment equipmentItems = equipment.getEquipment();
			equipmentItems.setItemInMainHand(mainHand);
			equipmentItems.setItemInOffHand(offHand);
			equipmentItems.setHelmet(helmet);
			equipmentItems.setChestplate(chestplate);
			equipmentItems.setLeggings(leggings);
			equipmentItems.setBoots(boots);
		} else {
			Location dump = new Location(location.getWorld(), 0, 0, 0);
			setLocation0(dump);
			if (equipment != null) {
				equipment.remove();
			}
		}
	}

	/**
	 * Set the item held in the main hand slot of this human entity.
	 */
	public void setMainHand(ItemStack itemStack) {
		this.mainHand = itemStack;
		if (visible) {
			equipment.getEquipment().setItemInMainHand(itemStack);
		}
	}

	/**
	 * Set the item held in the off hand slot of this human entity.
	 */
	public void setOffHand(ItemStack itemStack) {
		this.offHand = itemStack;
		if (visible) {
			equipment.getEquipment().setItemInOffHand(itemStack);
		}
	}

	/**
	 * Set the item held in the helmet slot of this human entity.
	 */
	public void setHelmet(ItemStack itemStack) {
		this.helmet = itemStack;
		if (visible) {
			equipment.getEquipment().setHelmet(itemStack);
		}
	}

	/**
	 * Set the item held in the chestplate slot of this human entity.
	 */
	public void setChestplate(ItemStack itemStack) {
		this.chestplate = itemStack;
		if (visible) {
			equipment.getEquipment().setChestplate(itemStack);
		}
	}

	/**
	 * Set the item held in the leggings slot of this human entity.
	 */
	public void setLeggings(ItemStack itemStack) {
		this.leggings = itemStack;
		if (visible) {
			equipment.getEquipment().setLeggings(itemStack);
		}
	}

	/**
	 * Set the item held in the boots slot of this human entity.
	 */
	public void setBoots(ItemStack itemStack) {
		this.boots = itemStack;
		if (visible) {
			equipment.getEquipment().setBoots(itemStack);
		}
	}

	/**
	 * Animate this human entity's hand.
	 */
	public void swingHand() {
		PacketPlayOutAnimation swingHand = new PacketPlayOutAnimation(entityPlayer, 0);
		for (int i = 0; i < viewers.size(); i++) {
			PlayerConnection playerConnection = ((CraftPlayer) viewers.get(i)).getHandle().playerConnection;
			playerConnection.sendPacket(swingHand);
		}
	}

	/**
	 * Makes the skin light up red. Not yet implemented.
	 */
	public void hurt() {
	}

	/**
	 * Call this if you don't want to use this anymore.
	 */
	public void dispose() {
		humanEntities.remove(this);
	}

}
