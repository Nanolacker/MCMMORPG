package com.mcmmorpg.common.navigation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	private final PlayerCharacter pc;
	private final ItemStack itemStack;
	private MapSegment mapSegment;
	private boolean isOpen;
	private ItemStack pcWeaponItemStackTemp;

	public PlayerCharacterMap(PlayerCharacter pc) {
		this.pc = pc;
		this.itemStack = new ItemStack(Material.FILLED_MAP);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.YELLOW + "Map");
		MapMeta meta = ((MapMeta) itemMeta);
		MapView mapView = Bukkit.createMap(pc.getWorld());
		mapView.setLocked(true);
		meta.setMapView(mapView);
		itemStack.setItemMeta(meta);
		itemStack.setItemMeta(itemMeta);

		this.mapSegment = null;
		this.isOpen = false;
		pcWeaponItemStackTemp = null;
	}

	public MapSegment getMapSegment() {
		return mapSegment;
	}

	public void setMapSegment(MapSegment mapSegment) {
		this.mapSegment = mapSegment;
		update();
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
		isOpen = true;
		update();
		inventory.setItemInMainHand(itemStack);
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
		isOpen = false;
	}

	public void update() {
		if (!isOpen) {
			return;
		}

		MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
		MapView mapView = mapMeta.getMapView();

		for (MapRenderer renderer : mapView.getRenderers()) {
			mapView.removeRenderer(renderer);
		}

		MapRenderer renderer = new MapRenderer() {
			@Override
			public void render(MapView map, MapCanvas canvas, Player player) {
				if (mapSegment != null) {
					mapSegment.render(canvas, pc);
				}
				String zoneText = StringUtility.chatColorToMapColor(pc.getZone());
				canvas.drawText(0, 0, MinecraftFont.Font, zoneText);
			}
		};
		mapView.addRenderer(renderer);
	}

}
