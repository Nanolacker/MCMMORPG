package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

/**
 * Allows for the creation of non-player human entities.
 */
public class NonPlayerHumanEntity {

	private final String name;
	private Location location;
	private GameProfile gameProfile;
	private EntityPlayer entityPlayer;
	private String textureData;
	private String textureSignature;
	private final List<Player> viewers;

	public NonPlayerHumanEntity(String name, Location location, String textureData, String textureSignature) {
		this.name = name;
		this.location = location;
		this.textureData = textureData;
		this.textureSignature = textureSignature;
		viewers = new ArrayList<>();
	}

	public void spawn() {
		MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
		this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
		this.gameProfile.getProperties().put("textures", new Property("textures", textureData, textureSignature));
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);

		this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
		this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
	}

	public void remove() {
		for (Player player : viewers) {
			hide(player);
		}
		entityPlayer.killEntity();
		viewers.clear();
	}

	public void show(Player player) {
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		playerConnection.sendPacket(addPlayerPacket);
		playerConnection.sendPacket(spawnPacket);
		viewers.add(player);
	}

	public void hide(Player player) {
		// PacketPlayOut spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		viewers.remove(player);
	}

	public Location getLocation() {
		return location.clone();
	}

	public void setLocation(Location location) {
		this.location = location;
		entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		for (Player player : viewers) {
			PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
			PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityPlayer);
			// Convert from Bukkit to approximately NMS yaw.
			PacketPlayOutEntityHeadRotation headRotatePacket = new PacketPlayOutEntityHeadRotation(entityPlayer,
					(byte) (0.7 * location.getYaw()));
			playerConnection.sendPacket(teleportPacket);
			playerConnection.sendPacket(headRotatePacket);
		}
	}

}
