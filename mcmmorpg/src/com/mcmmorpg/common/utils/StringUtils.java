package com.mcmmorpg.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class StringUtils {

	/**
	 * The ideal amount of characters per line to display in the lore of an item.
	 */
	public static final int STANDARD_LINE_LENGTH = 18;

	private StringUtils() {
	}

	public static List<String> lineSplit(String text) {
		return lineSplit(text, STANDARD_LINE_LENGTH);
	}

	public static String repeat(String s, int n) {
		String result = "";
		for (int i = 0; i < n; i++) {
			result += s;
		}
		return result;
	}

	public static List<String> lineSplit(String text, int lineLength) {
		if (text == null) {
			return null;
		}
		List<String> paragraph = new ArrayList<>();
		String[] preLines = text.split("\n");
		String prevColor = ChatColor.RESET.toString();
		for (String preLine : preLines) {
			Scanner lineParser = new Scanner(preLine);
			String line = prevColor;
			while (lineParser.hasNext()) {
				String token = lineParser.next();
				// +1 for space
				boolean lineLengthExceeded = line.length() + 1 + token.length() > lineLength;
				if (lineLengthExceeded) {
					line = line.trim();
					line = prevColor + line;
					paragraph.add(line);
					prevColor = ChatColor.getLastColors(line);
					line = prevColor + token;
				} else {
					line += token + " ";
				}
			}
			line = line.trim();
			paragraph.add(line);
			prevColor = ChatColor.getLastColors(line);
			lineParser.close();
		}
		return paragraph;
	}

}
