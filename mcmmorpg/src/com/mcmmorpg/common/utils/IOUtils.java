package com.mcmmorpg.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mcmmorpg.common.MMORPGPlugin;

public class IOUtils {

	private static final Gson gson = new Gson();

	private IOUtils() {
		// no instances
	}

	/**
	 * Writes the specified object to the file in JSON format. Throws an exception
	 * if the file does not exist.
	 */
	public static void objectToJsonFile(File file, Object obj) {
		String json = gson.toJson(obj);
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
	 * Returns an object constructed from the JSON found in the specified file.
	 * Throws an exception if the file does not exist.
	 */
	public static <T> T objectFromJsonFile(File file, Class<? extends T> clazz) {
		try (FileReader fileReader = new FileReader(file)) {
			JsonReader jsonReader = new JsonReader(fileReader);
			return gson.fromJson(jsonReader, clazz);
		} catch (IOException e) {
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
	 * Returns an object constructed from the specified JSON.
	 */
	public static <T> T objectFromJson(String json, Class<? extends T> clazz) {
		return gson.fromJson(json, clazz);
	}

	/**
	 * The folder in which the plugin's data should be contained.
	 */
	public static File getDataFolder() {
		File dataFolder = MMORPGPlugin.getPlugin().getDataFolder();
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		return dataFolder;
	}

	public static void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
