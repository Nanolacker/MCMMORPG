package com.mcmmorpg.common.navigation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.util.Debug;

public class PlayerCharacterMap {

	private final PlayerCharacter pc;
	private final ItemStack itemStack;
	private PlayerCharacterMapSegment mapSegment;
	private boolean isOpen;

	public PlayerCharacterMap(PlayerCharacter pc) {
		this.pc = pc;
		this.itemStack = new ItemStack(Material.FILLED_MAP);
		this.mapSegment = null;
		this.isOpen = false;
	}

	public PlayerCharacterMapSegment getMapSegment() {
		return mapSegment;
	}

	public void setMapSegment(PlayerCharacterMapSegment mapSegment) {
		this.mapSegment = mapSegment;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void open() {
		Player player = pc.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setItemInMainHand(itemStack);
		isOpen = true;
		update();
	}

	public void close() {
		Player player = pc.getPlayer();
		PlayerInventory inventory = player.getInventory();
		// inventory.setItemInMainHand(pc.getWeapon().getItemStack());
		isOpen = false;
	}

	public void update() {
		if (mapSegment == null || !isOpen) {
			return;
		}
		Debug.log("update");
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(mapSegment.getZone());
		itemStack.setItemMeta(itemMeta);
		MapView mapView = Bukkit.createMap(pc.getWorld());
		for (MapRenderer renderer : mapView.getRenderers()) {
			mapView.removeRenderer(renderer);
		}

		MapRenderer renderer = new MapRenderer() {
			@Override
			public void render(MapView map, MapCanvas canvas, Player player) {
				mapSegment.render(canvas, pc);
			}
		};
		mapView.addRenderer(renderer);

		MapMeta meta = ((MapMeta) itemStack.getItemMeta());
		meta.setMapView(mapView);
		itemStack.setItemMeta(meta);
	}

}
