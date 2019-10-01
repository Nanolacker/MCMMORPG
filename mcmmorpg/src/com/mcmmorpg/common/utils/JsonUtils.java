package com.mcmmorpg.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonUtils {

	private JsonUtils() {
		// no instances
	}

	public static <T> T jsonFromFile(File file, Class<? extends T> clazz) {
		try {
			Gson gson = new Gson();
			FileReader fileReader = new FileReader(file);
			JsonReader jsonReader = new JsonReader(fileReader);
			return gson.fromJson(jsonReader, clazz);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
