package com.mcmmorpg.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;

import com.google.gson.Gson;

public class JsonUtils {

	private JsonUtils() {
		// no instances
	}

	public static <T> T jsonFromResource(URL resource, Class<? extends T> clazz) {
		System.out.println("resource: " + resource.toString());
		try (InputStreamReader reader = new InputStreamReader(resource.openStream(), "UTF-8")) {
			//BufferedReader br = new BufferedReader(reader);
//			Stream<String> lines = br.lines();
//			for (Object line : lines.toArray()) {
//				System.out.println("BOP: " + line);
//			}
			Gson gson = new Gson();
			return gson.fromJson(reader, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
