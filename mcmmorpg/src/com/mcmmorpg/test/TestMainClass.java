package com.mcmmorpg.test;

import java.util.List;

import com.mcmmorpg.common.utils.StringUtils;

public class TestMainClass {

	public static void main(String[] args) {
		String text = "line1\nline2";
		List<String> lines = StringUtils.lineSplit(text, 5);
		for (String line : lines) {
			System.out.println(line);
		}
	}

}
