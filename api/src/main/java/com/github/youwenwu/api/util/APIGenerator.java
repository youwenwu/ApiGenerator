package com.github.youwenwu.api.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class APIGenerator {
	static String path = "cn/htd/middleware/openapi/controller/";

	public static List<String> getFiles(File dir) {
		List<String> list = new ArrayList<String>();
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				list.addAll(getFiles(file));
			} else if (file.getName().endsWith(".class")) {
				list.add(file.getPath());
			}
		}

		return list;
	}

	public static String formatClassName(String classpath, String className) {
		String result = className.replaceFirst(Matcher.quoteReplacement(classpath), "").replace(File.separator, ".").replaceAll(".class", "");

		if (result.startsWith(".")) {
			return result.replaceFirst(".", "");
		}
		return result;
	}
}
