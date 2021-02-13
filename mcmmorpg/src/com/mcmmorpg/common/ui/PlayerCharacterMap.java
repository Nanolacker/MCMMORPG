package com.mcmmorpg.common.ui;

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
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterCloseMapEvent;
import com.mcmmorpg.common.event.PlayerCharacterOpenMapEvent;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.util.StringUtility;

public class PlayerCharacterMap {
    /**
     * How far player characters must travel from their previous location to render
     * their map squared.
     */
    private static final double MINIMUM_DISTANCE_THRESHOLD_SQUARED = 4;
    private static final int ZONE_TEXT_LEFT_PADDING = 4;
    private static final int ZONE_TEXT_TOP_PADDING = 4;

    private final PlayerCharacter pc;
    private final ItemStack itemStack;
    private MapSegment mapSegment;
    private boolean isOpen;
    private Location previousLocation;
    /**
     * Store weapon when map is opened.
     */
    private Weapon pcWeaponTemp;

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
                Location currentLocation = pc.getLocation();
                boolean shouldRender = currentLocation
                        .distanceSquared(previousLocation) > MINIMUM_DISTANCE_THRESHOLD_SQUARED;
                if (!shouldRender) {
                    return;
                }

                previousLocation = currentLocation;
                if (mapSegment != null) {
                    mapSegment.render(canvas, pc);
                }

                String zoneText = StringUtility.chatColorToMapColor(pc.getZone());
                canvas.drawText(ZONE_TEXT_LEFT_PADDING, ZONE_TEXT_TOP_PADDING, MinecraftFont.Font, zoneText);
            }
        };
        mapView.addRenderer(renderer);

        this.mapSegment = null;
        this.isOpen = false;
        this.previousLocation = new Location(pc.getWorld(), 0, 0, 0);
        pcWeaponTemp = null;
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
        pcWeaponTemp = pc.getWeapon();
        Player player = pc.getPlayer();
        PlayerInventory inventory = player.getInventory();
        inventory.setItemInMainHand(itemStack);
        isOpen = true;
        PlayerCharacterOpenMapEvent event = new PlayerCharacterOpenMapEvent(pc);
        EventManager.callEvent(event);
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
        inventory.setItemInMainHand(pcWeaponTemp.getItemStack());
        pcWeaponTemp = null;
        isOpen = false;
        PlayerCharacterCloseMapEvent event = new PlayerCharacterCloseMapEvent(pc);
        EventManager.callEvent(event);
    }

    /**
     * Returns the player character's weapon if their map is open, null otherwise.
     */
    public Weapon getPlayerCharacterWeapon() {
        return pcWeaponTemp;
    }
}
