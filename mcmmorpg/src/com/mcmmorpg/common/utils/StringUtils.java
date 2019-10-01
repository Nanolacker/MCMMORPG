package com.mcmmorpg.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class StringUtils {

	/**
	 * The ideal amount of characters per line to display in the {@code lore} of an
	 * {@code ItemStack}.
	 */
	public static final int STANDARD_LINE_LENGTH = 18;

	private StringUtils() {
		// not to be instantiated
	}

	public static List<String> paragraph(String text) {
		return paragraph(text, STANDARD_LINE_LENGTH);
	}

	public static List<String> paragraph(String text, int lineLength) {
		List<String> paragraph = new ArrayList<>();
		String[] preLines = text.split("\n");
		String prevColor = ChatColor.RESET.toString();
		for (String preLine : preLines) {
			Scanner lineParser = new Scanner(preLine);
			String line = prevColor;
			while (lineParser.hasNext()) {
				String token = lineParser.next();
				boolean lineLengthExceeded = line.length() + 1 + token.length() > lineLength;
				if (lineLengthExceeded) { // +1 for space
					paragraph.add(line);
					prevColor = ChatColor.getLastColors(line);
					line = prevColor + token;
				} else {
					line += " " + token;
				}
			}
			prevColor = ChatColor.getLastColors(line);
			paragraph.add(line);
			lineParser.close();
		}
		return paragraph;
	}

}
