package com.mcmmorpg.impl.constants;

import java.awt.image.BufferedImage;
import java.io.File;

import org.bukkit.Location;

import com.mcmmorpg.common.navigation.MapSegment;
import com.mcmmorpg.common.util.IOUtility;

public class Maps {

	public static final MapSegment ELADRADOR;
	public static final MapSegment FLINTON_SEWERS;
	public static final MapSegment MELCHER_TAVERN_BASEMENT;

	static {
		File mapImagesDir = new File(IOUtility.getDataFolder(), "resources/mapImages");

		Location eladradorOrigin = RespawnLocations.MELCHER;
		File eladradorImageFile = new File(mapImagesDir, "Eladrador.png");
		BufferedImage eladradorImage = IOUtility.readImageFile(eladradorImageFile);
		ELADRADOR = new MapSegment(eladradorOrigin, eladradorImage);

		Location melcherTavernBasementOrigin = new Location(Worlds.ELADRADOR, -1088, 69, 238);
		File melcherTavernBasementImageFile = new File(mapImagesDir, "MelcherTavernBasement.jpg");
		BufferedImage melcherTavernBasementImage = IOUtility.readImageFile(melcherTavernBasementImageFile);
		MELCHER_TAVERN_BASEMENT = new MapSegment(melcherTavernBasementOrigin, melcherTavernBasementImage);

		Location flintonSewersOrigin = new Location(Worlds.ELADRADOR, -460, 44, -50);
		File flintonSewersImageFile = new File(mapImagesDir, "FlintonSewers.png");
		BufferedImage flintonSewersImage = IOUtility.readImageFile(flintonSewersImageFile);
		FLINTON_SEWERS = new MapSegment(flintonSewersOrigin, flintonSewersImage);
	}

}
