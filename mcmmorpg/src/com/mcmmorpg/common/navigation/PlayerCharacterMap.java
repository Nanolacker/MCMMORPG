package com.mcmmorpg.common.navigation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.util.StringUtility;

public class PlayerCharacterMap {

	/**
	 * How far player characters must travel from their previous location to render
	 * their map squared.
	 */
	private static final double MINIMUM_DISTANCE_THRESHOLD_SQUARED = 4;

	private final PlayerCharacter pc;
	private final ItemStack itemStack;
	private MapSegment mapSegment;
	private boolean isOpen;
	private Location previousLocation;
	private ItemStack pcWeaponItemStackTemp;

	public PlayerCharacterMap(PlayerCharacter pc) {
		this.pc = pc;
		this.itemStack = new ItemStack(Material.FILLED_MAP);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.YELLOW + "Map");
		MapMeta mapMeta = ((MapMeta) itemMeta);
		MapView mapView = Bukkit.createMap(pc.getWorld());
		mapView.setLocked(true);
		mapMeta.setMapView(mapView);
		itemStack.setItemMeta(mapMeta);

		for (MapRenderer renderer : mapView.getRenderers()) {
			mapView.removeRenderer(renderer);
		}

		MapRenderer renderer = new MapRenderer() {
			@Override
			public void render(MapView map, MapCanvas canvas, Player player) {
				if (mapSegment == null) {
					return;
				}
				Location currentLocation = pc.getLocation();
				boolean shouldRender = currentLocation
						.distanceSquared(previousLocation) > MINIMUM_DISTANCE_THRESHOLD_SQUARED;
				if (!shouldRender) {
					return;
				}
				previousLocation = currentLocation;
				mapSegment.render(canvas, pc);
				String zoneText = StringUtility.chatColorToMapColor(pc.getZone());
				canvas.drawText(0, 0, MinecraftFont.Font, zoneText);
			}
		};
		mapView.addRenderer(renderer);

		this.mapSegment = null;
		this.isOpen = false;
		this.previousLocation = new Location(pc.getWorld(), 0, 0, 0);
		pcWeaponItemStackTemp = null;
	}

	public MapSegment getMapSegment() {
		return mapSegment;
	}

	public void setMapSegment(MapSegment mapSegment) {
		this.mapSegment = mapSegment;
	}

	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Does nothing if the map is already open.
	 */
	public void open() {
		if (isOpen) {
			return;
		}
		pcWeaponItemStackTemp = pc.getWeapon().getItemStack();
		Player player = pc.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setItemInMainHand(itemStack);
		isOpen = true;
	}

	/**
	 * Does nothing if the map is already closed.
	 */
	public void close() {
		if (!isOpen) {
			return;
		}
		Player player = pc.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setItemInMainHand(pcWeaponItemStackTemp);
		pcWeaponItemStackTemp = null;
		isOpen = false;
	}

}
