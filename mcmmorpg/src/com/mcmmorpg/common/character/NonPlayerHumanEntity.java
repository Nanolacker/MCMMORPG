package com.mcmmorpg.common.character;

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
	private String texture;
	private String signature;

	public NonPlayerHumanEntity(String name, Location location, String texture, String signature) {
		this.name = name;
		this.location = location;
		this.texture = texture;
		this.signature = signature;
	}

	public void spawn() {
		MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
		this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
		this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);

		this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
		this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
	}

	public void despawn() {

	}

	public void remove() {
		entityPlayer.killEntity();
	}

	public void show(Player player) {
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		playerConnection.sendPacket(packetPlayOutPlayerInfo);
		playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
	}

	public void hide(Player player) {

	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
