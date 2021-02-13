package com.mcmmorpg.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmmorpg.common.MMORPGPlugin;

/**
 * Useful utilities for file processing and JSON parsing.
 */
public class IOUtility {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private IOUtility() {
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

    /**
     * Returns an object constructed from the JSON found in the specified file.
     * Throws an exception if the file does not exist. THIS CANNOT PARSE THE
     * CHARACTER '?'!
     */
    public static <T> T readJsonFile(File file, Class<? extends T> clazz) {
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
        return fromJson(json, clazz);
    }

    /**
     * Returns an object constructed from the specified JSON.
     */
    public static <T> T fromJson(String json, Class<? extends T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Writes the specified object to the file in JSON format. Throws an exception
     * if the file does not exist. THIS CANNOT PARSE THE CHARACTER '?'!
     */
    public static void writeJsonFile(File file, Object obj) {
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
     * Returns the image from the specified file.
     */
    public static BufferedImage readImageFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
