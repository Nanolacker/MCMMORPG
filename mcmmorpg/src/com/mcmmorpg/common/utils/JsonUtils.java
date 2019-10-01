package com.mcmmorpg.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonUtils {

	private static final Gson gson = new Gson();

	private JsonUtils() {
		// no instances
	}

	public static <T> T readFromFile(File file, Class<? extends T> clazz) {
		try {
			FileReader fileReader = new FileReader(file);
			JsonReader jsonReader = new JsonReader(fileReader);
			return gson.fromJson(jsonReader, clazz);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeToFile(File file, Object obj) {
		String json = gson.toJson(obj);
		try (PrintStream ps = new PrintStream(file)) {
			ps.print(json);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
