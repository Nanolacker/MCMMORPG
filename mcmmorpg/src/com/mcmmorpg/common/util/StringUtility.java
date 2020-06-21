package com.mcmmorpg.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

public class StringUtility {

	/**
	 * The ideal amount of characters per line to display in the lore of an item.
	 */
	public static final int STANDARD_LINE_LENGTH = 18;
	private static final Map<ChatColor, Integer> CHAT_COLOR_TO_MAP_COLOR_CODE = new HashMap<>();

	static {
		CHAT_COLOR_TO_MAP_COLOR_CODE.put(ChatColor.YELLOW, 44);
	}

	private StringUtility() {
	}

	public static String repeat(String s, int n) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < n; i++) {
			result.append(s);
		}
		return result.toString();
	}

	public static List<String> lineSplit(String text) {
		return lineSplit(text, STANDARD_LINE_LENGTH);
	}

	/**
	 * Splits the text into lines while preserving chat color.
	 */
	public static List<String> lineSplit(String text, int lineLength) {
		if (text == null) {
			return null;
		}
		if (lineLength == 0) {
			throw new IllegalArgumentException("Line length of 0");
		}
		List<String> lines = new ArrayList<>();
		String[] preLines = text.split("\n");
		String chatColor = "";
		for (String preLine : preLines) {
			int currentLineLength = 0;
			String[] tokens = preLine.split(" ");
			String line = chatColor;
			for (String token : tokens) {
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
					// ++ for space
					currentLineLength++;
				}
			}
			// add the last remaining line
			line = line.trim();
			lines.add(line);
			chatColor = ChatColor.getLastColors(line);
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
			if (ch == '�') {
				// skip next character
				i++;
			} else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Cuts the token so that its length does not exceed the line length.
	 */
	private static String cut(String token, int length) {
		int validCharCount = 0;
		char[] chars = token.toCharArray();
		int i;
		for (i = 0; i < token.length(); i++) {
			char ch = chars[i];
			if (ch == '�') {
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

	/**
	 * Returns whether the string is a number.
	 */
	public static boolean isNumeric(String s) {
		return s.matches("-?\\d+(\\.\\d+)?");
	}

	public static String chatColorToMapText(String text) {
		StringBuilder mapText = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '�') {
				i++;
				char chatColorCode = text.charAt(i);
				ChatColor chatColor = ChatColor.getByChar(chatColorCode);
				Integer mapColorCode = CHAT_COLOR_TO_MAP_COLOR_CODE.get(chatColor);
				if (mapColorCode != null) {
					String mapColor = "§" + mapColorCode + ";";
					mapText.append(mapColor);
				}
			} else {
				mapText.append(ch);
			}
		}
		Debug.log(mapText.toString());
		return mapText.toString();
	}

}
