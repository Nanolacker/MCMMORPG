package com.mcmmorpg.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.mcmmorpg.common.MMORPGPlugin;

/**
 * Useful utilities for file processing and JSON parsing.
 */
public class IOUtils {

	private static final Gson gson = new Gson();

	private IOUtils() {
		// no instances
	}

	/**
	 * Returns an object constructed from the JSON found in the specified file.
	 * Throws an exception if the file does not exist. THIS CANNOT PASS THE
	 * CHARACTER '?'!
	 */
	public static <T> T readJson(File file, Class<? extends T> clazz) {
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String json = new String(bytes);
		// to account for an error in which chat color is not store properly
		json = json.replace((char) 65533, '§');
		json = json.replace('?', '§');
		return objectFromJson(json, clazz);
	}

	/**
	 * Returns an object constructed from the specified JSON.
	 */
	public static <T> T objectFromJson(String json, Class<? extends T> clazz) {
		return gson.fromJson(json, clazz);
	}

	/**
	 * Writes the specified object to the file in JSON format. Throws an exception
	 * if the file does not exist. THIS CANNOT PASS THE CHARACTER '?'!
	 */
	public static void writeJson(File file, Object obj) {
		String json = toJson(obj);
		if (!file.exists()) {
			createFile(file);
		}
		try (PrintStream ps = new PrintStream(file);) {
			ps.print(json);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns JSON representing the specified Object.
	 */
	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * The folder in which the plugin's data should be contained.
	 */
	public static File getDataFolder() {
		File dataFolder = MMORPGPlugin.getInstance().getDataFolder();
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		return dataFolder;
	}

	/**
	 * Creates the specified file.
	 */
	public static void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
