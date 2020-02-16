package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

public class NPCHuman {

	private static final double RENDER_RADIUS = 25;
	private static final double RENDER_PERIOD = 0.5;

	private final String name;
	private Location location;
	private EntityPlayer entityPlayer;
	private boolean visible;
	private final List<Player> viewers;
	private RepeatingTask renderer;

	public NPCHuman(String name, Location location, String textureData, String textureSignature) {
		this.name = name;
		this.location = location;
		MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(),
				ChatColor.translateAlternateColorCodes('&', this.name));
		gameProfile.getProperties().put("textures", new Property("textures", textureData, textureSignature));
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
		this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
		viewers = new ArrayList<>();
		setVisible(false);
		renderer = new RepeatingTask(RENDER_PERIOD) {
			@Override
			protected void run() {
				render();
			}
		};
		renderer.schedule();
	}

	private void render() {
		World world = location.getWorld();
		Collection<Entity> nearby = world.getNearbyEntities(location, RENDER_RADIUS, RENDER_RADIUS, RENDER_RADIUS);
		Entity[] nearby0 = nearby.toArray(new Entity[nearby.size()]);
		for (Entity entity : nearby0) {
			if (entity instanceof Player) {
				show((Player) entity);
			}
		}
	}

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

	public Location getLocation() {
		return location.clone();
	}

	public void setLocation(Location location) {
		this.location = location;
		if (visible) {
			setLocation0(location);
		}
	}

	private void setLocation0(Location location) {
		entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		for (int i = 0; i < viewers.size(); i++) {
			Player player = viewers.get(i);
			PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
			PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityPlayer);
			// Convert from Bukkit to approximately NMS yaw.
			PacketPlayOutEntityHeadRotation headRotatePacket = new PacketPlayOutEntityHeadRotation(entityPlayer,
					(byte) (0.7 * location.getYaw()));
			playerConnection.sendPacket(teleportPacket);
			playerConnection.sendPacket(headRotatePacket);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			setLocation0(location);
		} else {
			Location dump = new Location(location.getWorld(), 0, 512, 0);
			setLocation0(dump);
		}
	}

	public void swingHand() {
		ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
		entityPlayer.setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack));
		PacketPlayOutAnimation swingHand = new PacketPlayOutAnimation(entityPlayer, 0);
		for (int i = 0; i < viewers.size(); i++) {
			PlayerConnection playerConnection = ((CraftPlayer) viewers.get(i)).getHandle().playerConnection;
			playerConnection.sendPacket(swingHand);
		}
	}

	/**
	 * Makes the skin light up red.
	 */
	public void hurt() {
	}

	/**
	 * Call if you don't want to use this anymore!
	 */
	public void dispose() {
		renderer.cancel();
	}

}
