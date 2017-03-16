package com.github.youwenwu.api.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		String result = className.replaceFirst(classpath, "").replace(File.separator, ".").replaceAll(".class", "");

		if (result.startsWith(".")) {
			return result.replaceFirst(".", "");
		}
		return result;
	}

	/**
	 * 判断是否是基础类型
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(String type) {
		String[] types = { "java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
				"java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Character", "java.lang.String",
				"int", "double", "long", "short", "byte", "boolean", "char", "float", "java.math.BigDecimal", "java.util.Date"};
		List<String> list=Arrays.asList(types);  
		return list.contains(type);
	}
	
	/**
	 * 判断对象是否是集合类
	 * @param o
	 * @return
	 */
	public static boolean isCollection(String type) {
		String[] types = { "java.util.List"};
		List<String> list=Arrays.asList(types);  
		return list.contains(type);
	}
	
	/**
	 * 判断对象是否带泛型
	 * @param o
	 * @return
	 */
	public static boolean hasType(Field f) {
		String genericType = f.getGenericType().getTypeName();
		
		if (genericType.contains("<") & genericType.endsWith(">"))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取字段泛型
	 * @param f
	 * @return
	 */
	public static String getType(Field f)
	{
		String genericType = f.getGenericType().getTypeName();
		int beginIndex = genericType.indexOf("<");
		int endIndex = genericType.indexOf(">");
		return genericType.substring(beginIndex + 1, endIndex);
	}
	
	public static Map<String, Object> parseObjectToMap(Object o) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Class<?> c = o.getClass();
		Map<String, Object> result = new HashMap<String, Object>();
		for (Field f : c.getDeclaredFields())
		{
			//处理基础类型
			if (isBaseType(f.getType().getName()))
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", f.getName());
				map.put("type", f.getType().getSimpleName());
				result.put(f.getName(), map);
			}
			//处理封装类型
			else
			{
				//如果是集合类，处理泛型
				if (isCollection(f.getType().getName()) && hasType(f))
				{
					String genericType = getType(f);
					//如果泛型为？，则跳过
					if (genericType.equals("?") || isBaseType(genericType))
					{
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("value", f.getName());
						map.put("type", f.getType().getSimpleName());
						result.put(f.getName(), map);
					}
					//如果是普通泛型，则实例化泛型对象并解析字段
					else
					{
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("value", f.getName());
						map.put("type", f.getType().getSimpleName());
						map.put("detail", parseObjectToMap(Class.forName(genericType).newInstance()));
						result.put(f.getName(), map);
					}
					continue;
				}
				f.setAccessible(true);
				//如果为null，则停止遍历
				if (f.get(o) == null)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("value", f.getName());
					map.put("type", f.getType().getSimpleName());
					result.put(f.getName(), map);
				}
				//否则继续遍历字段对象
				else 
				{
					result.put(f.getName(), parseObjectToMap(f.get(o)));
				}
			}
		}
		return result;
	}
}
