package com.mcmmorpg.common.navigation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MinecraftFont;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;

/**
 * A map segment that players can view with their map. Each segment represents
 * 128 by 128 blocks and can display quest markers.
 */
public class MapSegment {

	private Location origin;
	private final BufferedImage image;
	private final List<QuestMarker> questMarkers;

	public MapSegment(Location origin, BufferedImage image) {
		this.origin = origin;
		this.image = image;
		if (image == null) {
			throw new NullPointerException("Image is null");
		}
		this.questMarkers = new ArrayList<>();
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

		Location mapCenter = pc.getLocation();
		int mapCenterX = mapCenter.getBlockX();
		int mapCenterZ = mapCenter.getBlockZ();

		drawMapImage(canvas, origin, image, mapCenter);

		byte direction = (byte) (CardinalDirection.forVector(mapCenter.getDirection()).getOctant() * 2 - 4);
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
			int questMarkerX = questMarkerLocation.getBlockX() - mapCenterX + 64;
			int questMarkerY = questMarkerLocation.getBlockZ() - mapCenterZ + 64;
			Quest quest = questMarker.getQuest();
			String questMarkerText = questMarker.getDisplayType(pc).getMapText(quest, pc);
			canvas.drawText(questMarkerX, questMarkerY, MinecraftFont.Font, questMarkerText);
		}
	}

	private static void drawMapImage(MapCanvas canvas, Location mapSegmentOrigin, BufferedImage image,
			Location mapCenter) {
		int originX = mapSegmentOrigin.getBlockX();
		int originZ = mapSegmentOrigin.getBlockZ();

		int mapCenterX = mapCenter.getBlockX();
		int mapCenterZ = mapCenter.getBlockZ();

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		int imageMapPosX;
		int imageMapPosY;

		int x1 = Math.max(mapCenterX - 192, originX);
		int x2 = Math.min(mapCenterX - 65, originX + imageWidth);
		if (x1 > x2) {
			imageMapPosX = originX - mapCenterX + 64;
			x1 = 0;
		} else {
			imageMapPosX = 0;
		}

		int z1 = Math.max(mapCenterZ - 192, originZ);
		int z2 = Math.min(mapCenterZ - 65, originZ + imageHeight);
		if (z1 > z2) {
			imageMapPosY = originZ - mapCenterZ + 64;
			z1 = 0;
		} else {
			imageMapPosY = 0;
		}

		int subimageX = Math.min(Math.max(0, mapCenterX - originX - 64), imageWidth - 1);
		int subimageY = Math.min(Math.max(0, mapCenterZ - originZ - 64), imageHeight - 1);
		int pixelWidth = Math.min(128, imageWidth - subimageX);
		int pixelHeight = Math.min(128, imageHeight - subimageY);

		BufferedImage subImage = image.getSubimage(subimageX, subimageY, pixelWidth, pixelHeight);
		canvas.drawImage(imageMapPosX, imageMapPosY, subImage);
	}

}
