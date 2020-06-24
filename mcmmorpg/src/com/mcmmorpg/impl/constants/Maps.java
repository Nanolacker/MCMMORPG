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
		File mapImagesFile = new File(IOUtility.getDataFolder(), "resources/mapImages");

		Location eladradorOrigin = RespawnLocations.MELCHER;
		File eladradorImageFile = new File(mapImagesFile, "eladrador.png");
		BufferedImage eladradorImage = IOUtility.readImageFile(eladradorImageFile);
		ELADRADOR = new MapSegment(eladradorOrigin, eladradorImage);

		Location melcherTavernBasementOrigin = new Location(Worlds.ELADRADOR, -1088, 69, 238);
		File melcherTavernBasementImageFile = new File(mapImagesFile, "melcherTavernBasement.jpg");
		BufferedImage melcherTavernBasementImage = IOUtility.readImageFile(melcherTavernBasementImageFile);
		MELCHER_TAVERN_BASEMENT = new MapSegment(melcherTavernBasementOrigin, melcherTavernBasementImage);

		Location flintonSewersOrigin = RespawnLocations.MELCHER;
		File flintonSewersImageFile = new File(mapImagesFile, "flintonSewers.png");
		BufferedImage flintonSewersImage = IOUtility.readImageFile(flintonSewersImageFile);
		FLINTON_SEWERS = new MapSegment(flintonSewersOrigin, flintonSewersImage);
	}

}
