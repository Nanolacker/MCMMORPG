package com.mcmmorpg.common.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MinecraftFont;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.ui.QuestMarker.QuestMarkerIcon;
import com.mcmmorpg.common.util.CardinalDirection;
import com.mcmmorpg.common.util.StringUtility;

/**
 * A map segment that players can view with their map. Each segment represents
 * 128 by 128 blocks and can display quest markers.
 */
public class MapSegment {

	private static final int QUEST_MARKER_ICON_RIGHT_PADDING = 6;
	private static final int QUEST_MARKER_ICON_LEFT_PADDING = 2;
	private static final int QUEST_MARKER_ICON_TOP_PADDING = 2;
	private static final int QUEST_MARKER_ICON_BOTTOM_PADDING = 8;

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
		Location mapCenter = pc.getLocation();

		clearCanvas(canvas);
		drawMapImage(canvas, image, origin, mapCenter);
		drawQuestMarkers(canvas, pc, questMarkers, origin, mapCenter);
		drawMapCursor(canvas, mapCenter);
	}

	private static void clearCanvas(MapCanvas canvas) {
		for (int r = 0; r < 128; r++) {
			for (int c = 0; c < 128; c++) {
				canvas.setPixel(r, c, (byte) -1);
			}
		}
	}

	private static void drawMapImage(MapCanvas canvas, BufferedImage image, Location mapSegmentOrigin,
			Location mapCenter) {
		int originX = mapSegmentOrigin.getBlockX();
		int originZ = mapSegmentOrigin.getBlockZ();

		int mapCenterX = mapCenter.getBlockX();
		int mapCenterZ = mapCenter.getBlockZ();

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		int subimageMapPosX;
		int subimageMapPosY;

		int x1 = Math.max(mapCenterX - 192, originX);
		int x2 = Math.min(mapCenterX - 65, originX + imageWidth);
		if (x1 > x2) {
			subimageMapPosX = originX - mapCenterX + 64;
			x1 = 0;
		} else {
			subimageMapPosX = 0;
		}

		int z1 = Math.max(mapCenterZ - 192, originZ);
		int z2 = Math.min(mapCenterZ - 65, originZ + imageHeight);
		if (z1 > z2) {
			subimageMapPosY = originZ - mapCenterZ + 64;
			z1 = 0;
		} else {
			subimageMapPosY = 0;
		}

		int subimageX = Math.min(Math.max(0, mapCenterX - originX - 64), imageWidth - 1);
		int subimageY = Math.min(Math.max(0, mapCenterZ - originZ - 64), imageHeight - 1);
		int subimageWidth = Math.min(128, imageWidth - subimageX);
		int subimageHeight = Math.min(128, imageHeight - subimageY);

		BufferedImage subImage = image.getSubimage(subimageX, subimageY, subimageWidth, subimageHeight);
		canvas.drawImage(subimageMapPosX, subimageMapPosY, subImage);
	}

	private static void drawQuestMarkers(MapCanvas canvas, PlayerCharacter pc, List<QuestMarker> questMarkers,
			Location mapSegmentOrigin, Location mapCenter) {
		int mapCenterX = mapCenter.getBlockX();
		int mapCenterZ = mapCenter.getBlockZ();

		for (QuestMarker questMarker : questMarkers) {
			QuestMarkerIcon icon = questMarker.getIcon(pc);
			if (icon == QuestMarkerIcon.HIDDEN) {
				continue;
			}
			Location questMarkerLocation = questMarker.getLocation();

			int offsetX = questMarkerLocation.getBlockX() - mapCenterX;
			int offsetZ = questMarkerLocation.getBlockZ() - mapCenterZ;

			double distanceSquared = offsetX * offsetX + offsetZ * offsetZ;
			if (distanceSquared > icon.getMapDisplayRangeSquared()) {
				continue;
			}

			double tan = (double) offsetZ / offsetX;

			if (offsetX >= 64 - QUEST_MARKER_ICON_RIGHT_PADDING) {
				offsetX = 63 - QUEST_MARKER_ICON_RIGHT_PADDING;
				offsetZ = (int) (offsetX * tan);
			} else if (offsetX < -64 + QUEST_MARKER_ICON_LEFT_PADDING) {
				offsetX = -64 + QUEST_MARKER_ICON_LEFT_PADDING;
				offsetZ = (int) (offsetX * tan);
			}
			if (offsetZ >= 64 - QUEST_MARKER_ICON_BOTTOM_PADDING) {
				offsetZ = 63 - QUEST_MARKER_ICON_BOTTOM_PADDING;
				offsetX = (int) (offsetZ / tan);
			} else if (offsetZ < -64 + QUEST_MARKER_ICON_TOP_PADDING) {
				offsetZ = -64 + QUEST_MARKER_ICON_TOP_PADDING;
				offsetX = (int) (offsetZ / tan);
			}

			Quest quest = questMarker.getQuest();
			String questMarkerText = StringUtility.chatColorToMapColor(questMarker.getIcon(pc).getText(quest, pc));

			canvas.drawText((int) offsetX + 64, (int) offsetZ + 64, MinecraftFont.Font, questMarkerText);
		}
	}

	private void drawMapCursor(MapCanvas canvas, Location mapCenter) {
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
	}

}
