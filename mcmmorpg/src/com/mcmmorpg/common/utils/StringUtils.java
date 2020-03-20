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

	public static String repeat(String s, int n) {
		String result = "";
		for (int i = 0; i < n; i++) {
			result += s;
		}
		return result;
	}

	/**
	 * Splits the text into lines. Chat color is preserved throughout lines.
	 */
	public static List<String> lineSplit(String text) {
		return lineSplit(text, STANDARD_LINE_LENGTH);
	}

	/**
	 * Splits the text into lines that do not exceed the specified line length. Chat
	 * color is preserved throughout lines.
	 */
	public static List<String> lineSplit(String text, int lineLength) {
		if (text == null) {
			return null;
		}
		List<String> lines = new ArrayList<>();
		String[] preLines = text.split("\n");
		String chatColor = "";
		for (String preLine : preLines) {
			int currentLineLength = 0;
			Scanner lineParser = new Scanner(preLine);
			String line = chatColor;
			while (lineParser.hasNext()) {
				String token = lineParser.next();
				int tokenLength = length(token);
				if (tokenLength > lineLength) {
					// if the token is too long, it will get cut off (stupid user)
					token = cut(token, lineLength);
					tokenLength = lineLength;
				}
				currentLineLength += tokenLength;
				boolean lineLengthExceeded = currentLineLength > lineLength;
				if (lineLengthExceeded) {
					// start new line
					line = line.trim();
					lines.add(line);
					chatColor += ChatColor.getLastColors(line);
					line = chatColor + token + " ";
					currentLineLength = length(line);
				} else {
					// add token to current line
					line += token + " ";
					// +1 for space
					currentLineLength++;
				}
			}
			// add the last remaining line
			line = line.trim();
			lines.add(line);
			chatColor = ChatColor.getLastColors(line);
			lineParser.close();
		}
		return lines;
	}

	/**
	 * Returns the length of the token, excluding chat color characters. Used for
	 * line splitting.
	 */
	private static int length(String token) {
		int count = 0;
		char[] chars = token.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (ch == '§') {
				// skip next character
				i++;
			} else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Assumes the token's length is greater than the specified length. Used for
	 * line splitting.
	 */
	private static String cut(String token, int length) {
		int validCharCount = 0;
		char[] chars = token.toCharArray();
		int i;
		for (i = 0; i < token.length(); i++) {
			char ch = chars[i];
			if (ch == '§') {
				// skip next character
				i++;
			} else {
				validCharCount++;
				if (validCharCount == length) {
					break;
				}
			}
		}
		return token.substring(0, i + 1);
	}

	public static boolean isNumeric(String s) {
		return s.matches("-?\\d+(\\.\\d+)?");
	}

}
