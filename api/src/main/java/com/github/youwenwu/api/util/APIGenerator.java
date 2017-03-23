package com.github.youwenwu.api.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.github.youwenwu.api.annotation.APIField;
import com.github.youwenwu.api.annotation.APIWrapperField;

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
	

	public static Map<String, Object> parseAPIField(APIField apiField)
	{
		//处理apiField基础数据
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldMap.put("name", apiField.name());
		fieldMap.put("type", apiField.type());
		fieldMap.put("desc", apiField.desc());
		fieldMap.put("required", apiField.required());
		fieldMap.put("refId", apiField.refId());
		fieldMap.put("example", apiField.example());
		
		//处理apiField class数据
		if (!apiField._class().equals(Object.class))
		{
			fieldMap.put("detail", parseClass(apiField._class()));
			fieldMap.put("refId", "");
		}
		
		return fieldMap;
	}
	
	public static List<Map<String, Object>> parseAPIWrapperField(APIWrapperField apiWrapperField)
	{
		List<Map<String, Object>> returnFieldList = new ArrayList<Map<String, Object>>();
		for (APIField apiField : apiWrapperField.fields())
		{
			returnFieldList.add(parseAPIField(apiField));
		}
		
		return returnFieldList;
	}
	
	public static List<Map<String, Object>> parseClass(Class<?> _class)
	{
		Field[] fields = _class.getDeclaredFields();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Field field : fields)
		{
			APIField classApiField = field.getDeclaredAnnotation(APIField.class);
			if (classApiField != null)
			{
				Map<String, Object> apiMap = parseAPIField(classApiField);
				APIWrapperField apiWrapperField = field.getDeclaredAnnotation(APIWrapperField.class);
				if (apiWrapperField != null)
				{
					apiMap.put("detail", parseAPIWrapperField(apiWrapperField));
					apiMap.put("refId", "");
				}
				list.add(apiMap);
			}
			
		}
		
		return list;
	}
}
