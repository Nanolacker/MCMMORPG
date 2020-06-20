package com.mcmmorpg.common.navigation;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MinecraftFont;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.util.Debug;

/**
 * A map segment that players can view with their map. Each segment represents
 * 128 by 128 blocks and can display quest markers.
 */
public class PlayerCharacterMapSegment {

	private final String zone;
	private int xMin, zMin, xMax, zMax;
	private final Image image;
	private final List<QuestMarker> questMarkers;

	public PlayerCharacterMapSegment(String zone, int xMin, int zMin, int xMax, int zMax, Image image) {
		this.zone = zone;
		this.xMin = xMin;
		this.zMin = zMin;
		this.xMax = xMax;
		this.zMax = zMax;
		this.image = image;
		if (image == null) {
			throw new NullPointerException("Image is null");
		}
		this.questMarkers = new ArrayList<>();
	}

	public String getZone() {
		return zone;
	}

	/**
	 * How many blocks wide this map segment is (length across x-axis).
	 */
	public int getWidth() {
		return xMax - xMin;
	}

	/**
	 * How many blocks high this map segment is (length across z-axis).
	 */
	public int getHeight() {
		return zMax - zMin;
	}

	public Image getImage() {
		return image;
	}

	public List<QuestMarker> getQuestMarkers() {
		return questMarkers;
	}

	public void addQuestMarker(QuestMarker questMarker) {
		questMarkers.add(questMarker);
	}

	void render(MapCanvas canvas, PlayerCharacter pc) {
		for (int r = 0; r < 128; r++) {
			for (int c = 0; c < 128; c++) {
				canvas.setPixel(r, c, (byte) -1);
			}
		}

		Location location = pc.getLocation();
		int pcX = location.getBlockX();
		int pcZ = location.getBlockZ();

		int imageX = xMax - pcX - 64;
		int imageY = zMax - pcZ - 64;
		canvas.drawImage(imageX, imageY, image);

		byte direction = (byte) (CardinalDirection.forVector(location.getDirection()).getOctant() * 2 - 4);
		if (direction < 0) {
			direction = (byte) (16 + direction);
		}
		MapCursorCollection cursors = canvas.getCursors();
		if (cursors.size() == 0) {
			canvas.getCursors().addCursor(0, 0, direction);
		} else {
			canvas.getCursors().getCursor(0).setDirection(direction);
		}

		for (QuestMarker questMarker : questMarkers) {
			int questMarkerX = questMarker.getLocation().getBlockX() - pcX + 64;
			int questMarkerY = questMarker.getLocation().getBlockZ() - pcZ + 64;
			canvas.drawText(questMarkerX, questMarkerY, MinecraftFont.Font, "§74;!");
		}
	}

}
