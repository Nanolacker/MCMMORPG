package com.mcmmorpg.impl.constants;

import java.awt.image.BufferedImage;
import java.io.File;

import org.bukkit.Location;

import com.mcmmorpg.common.navigation.MapSegment;
import com.mcmmorpg.common.util.IOUtility;

public class Maps {

	public static final MapSegment ELADRADOR;
	public static final MapSegment FLINTON_SEWERS;

	static {
		File mapImagesFile = new File(IOUtility.getDataFolder(), "resources/mapImages");

		Location eladradorOrigin = new Location(Worlds.ELADRADOR, -1400, 0, 51);
		File eladradorImageFile = new File(mapImagesFile, "eladrador.png");
		BufferedImage eladradorImage = IOUtility.readImageFile(eladradorImageFile);

		Location flintonSewersOrigin =  new Location(Worlds.ELADRADOR, -1400, 0, 51);
		File flintonSewersImageFile = new File(mapImagesFile, "flintonSewers.png");
		BufferedImage flintonSewersImage = IOUtility.readImageFile(flintonSewersImageFile);

		ELADRADOR = new MapSegment(eladradorOrigin, eladradorImage);
		FLINTON_SEWERS = new MapSegment(flintonSewersOrigin, flintonSewersImage);
	}

}
