package com.mcmmorpg.common.navigation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MinecraftFont;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * A map segment that players can view with their map. Each segment represents
 * 128 by 128 blocks and can display quest markers.
 */
public class MapSegment {

	private Location origin;
	private final BufferedImage image;
	private final List<QuestMarker> questMarkers;
	private final int imageSemiWidth;

	public MapSegment(Location origin, BufferedImage image) {
		this.origin = origin;
		this.image = image;
		if (image == null) {
			throw new NullPointerException("Image is null");
		}
		this.questMarkers = new ArrayList<>();
		this.imageSemiWidth = image.getWidth() / 2;
	}

	public BufferedImage getImage() {
		return image;
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

		int mapOriginX = origin.getBlockX() + imageSemiWidth;
		int mapOriginZ = origin.getBlockZ() + imageSemiWidth;

		Location location = pc.getLocation();
		int pcX = location.getBlockX();
		int pcZ = location.getBlockZ();

		int imageX = mapOriginX - pcX;
		int imageY = mapOriginZ - pcZ;
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
			Location questMarkerLocation = questMarker.getLocation();
			int questMarkerX = questMarkerLocation.getBlockX() - pcX + 64;
			int questMarkerY = questMarkerLocation.getBlockZ() - pcZ + 64;
			String questMarkerText = questMarker.getDisplayType(pc).getMapText();
			canvas.drawText(questMarkerX, questMarkerY, MinecraftFont.Font, questMarkerText);
		}
	}

}
