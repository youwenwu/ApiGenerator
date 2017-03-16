package com.github.youwenwu.api.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.youwenwu.api.annotation.APIField;
import com.github.youwenwu.api.annotation.APIParameter;
import com.github.youwenwu.api.annotation.APIReturn;
import com.github.youwenwu.api.annotation.APIWrapperField;
import com.github.youwenwu.api.annotation.APISummary;
import com.github.youwenwu.api.util.APIGenerator;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@RestController
@RequestMapping("/api")
public class APIController {
	Logger logger = LoggerFactory.getLogger(APIController.class);

	@RequestMapping(value = "")
	public void apiList(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException, IOException, TemplateException {
		List<Object> list = new ArrayList<Object>();
		// 获取项目classpath路径下的所有class文件
		File classpath = new File(APIController.class.getClassLoader().getResource("").getFile());
		List<String> fileList = APIGenerator.getFiles(classpath);

		for (String classFile : fileList) {
			Class<?> c = Class.forName(APIGenerator.formatClassName(classpath.getPath(), classFile));

			for (Method m : c.getDeclaredMethods()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("className", c.getCanonicalName());
				APISummary apiSummary = m.getDeclaredAnnotation(APISummary.class);
				if (apiSummary != null)
				{
					map.put("name", apiSummary.name());
					map.put("url", apiSummary.url());
					map.put("methodName", m.getName());
					map.put("methodType", apiSummary.methodType());
					list.add(map);
				}
			}

		}
		// 初始化freemarker配置
		Configuration config = new Configuration(Configuration.VERSION_2_3_23);
		config.setClassForTemplateLoading(this.getClass(), "");
		// 渲染freemarker
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("list", list);

		String url = request.getRequestURL().toString();
		root.put("contextPath", url.replaceAll(request.getRequestURI(), "") + request.getContextPath());
		Template t = config.getTemplate("apiList.ftl");
		response.setContentType("text/html; charset=" + t.getEncoding());
		PrintWriter out = response.getWriter();

		t.process(root, out);
	}

	@RequestMapping(value = "/apiDetail")
	public void apiDetail(@RequestParam(value = "className") String className,
			@RequestParam(value = "methodName") String methodName, HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {

		Class<?> c = Class.forName(className);
		// 获取类注解路径
		ObjectMapper mapper = new ObjectMapper();

		for (Method m : c.getDeclaredMethods()) {
			if (!m.getName().equals(methodName)) {
				continue;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			// 方法注解
			APISummary apiSummary = m.getDeclaredAnnotation(APISummary.class);
			if (apiSummary != null)
			{
				map.put("name", apiSummary.name());
				map.put("url", apiSummary.url());
				map.put("methodName", m.getName());
				map.put("methodType", apiSummary.methodType());
			}

			// 获取入参
			APIParameter apiParameter = m.getDeclaredAnnotation(APIParameter.class);
			if (apiParameter != null)
			{
				List<Map<String, Object>> parameters = new ArrayList<Map<String, Object>>();
				for (APIField apiField : apiParameter.parameters())
				{
					parameters.add(parseAPIField(apiField));
				}
				map.put("parameters", parameters);
			}

			// 获取返回类型
			APIReturn apiReturn = m.getDeclaredAnnotation(APIReturn.class);
			if (apiReturn != null)
			{
				List<Map<String, Object>> returnFieldList = new ArrayList<Map<String, Object>>();
				for (APIField apiField : apiReturn.fields())
				{
					returnFieldList.add(parseAPIField(apiField));
				}
				map.put("returnFieldList", mapper.writeValueAsString(returnFieldList));
			}
			
			//获取返回类型detail
			APIWrapperField[] apiWrapperFields = m.getDeclaredAnnotationsByType(APIWrapperField.class);
			if (apiWrapperFields != null)
			{
				Map<String, Object> returnDetailMap = new HashMap<String, Object>();
				for (APIWrapperField apiWrapperField : apiWrapperFields)
				{
					returnDetailMap.put(apiWrapperField.id(), parseAPIWrapperField(apiWrapperField));
				}
				map.put("returnDetailMap", mapper.writeValueAsString(returnDetailMap));
			}

			// 初始化freemarker配置
			Configuration config = new Configuration(Configuration.VERSION_2_3_23);
			config.setClassForTemplateLoading(this.getClass(), "");
			// 渲染freemarker
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("detail", map);

			String url = request.getRequestURL().toString();
			root.put("contextPath", url.replaceAll(request.getRequestURI(), "") + request.getContextPath());
			Template t = config.getTemplate("apiDetail.ftl");
			response.setContentType("text/html; charset=" + t.getEncoding());
			PrintWriter out = response.getWriter();

			t.process(root, out);

		}
	}
	
	private Map<String, Object> parseAPIField(APIField apiField)
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
			Field[] fields = apiField._class().getDeclaredFields();
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
			fieldMap.put("detail", list);
			fieldMap.put("refId", "");
		}
		
		return fieldMap;
	}
	
	private List<Map<String, Object>> parseAPIWrapperField(APIWrapperField apiWrapperField)
	{
		List<Map<String, Object>> returnFieldList = new ArrayList<Map<String, Object>>();
		for (APIField apiField : apiWrapperField.fields())
		{
			returnFieldList.add(parseAPIField(apiField));
		}
		
		return returnFieldList;
	}
}
