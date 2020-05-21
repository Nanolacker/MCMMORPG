package com.mcmmorpg.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LocationGenerator {

	public static void main(String[] args) {
		List<Location> locations = new ArrayList<>();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter name of world");
		String world = scanner.next().toUpperCase();
		while (true) {
			Location currentLocation = new Location(world);
			System.out.println("x? type \"print\" to finish");
			String input = scanner.next();
			if (input.equalsIgnoreCase("print")) {
				for (Location location : locations) {
					System.out.println(location + ",");
				}
				break;
			} else {
				currentLocation.x = Double.parseDouble(input);
			}
			System.out.println("y?");
			currentLocation.y = scanner.nextDouble();
			System.out.println("z?");
			currentLocation.z = scanner.nextDouble();
			locations.add(currentLocation);
		}
		scanner.close();
	}

	static class Location {
		String world;
		double x, y, z;

		Location(String name) {
			this.world = name;
		}

		@Override
		public String toString() {
			return String.format("new Location(Worlds.%s, %f, %f, %f)", world, x, y, z);
		}
	}

}
