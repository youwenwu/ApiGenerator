package cn.bluesky.api.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.bluesky.api.util.APIGenerator;
import cn.bluesky.api.util.APIParameter;
import cn.bluesky.api.util.APIResultRelation;
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

	@RequestMapping(value = "", name = "获取api列表")
	public void apiList(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException, IOException, TemplateException {
		List<Object> list = new ArrayList<Object>();
		// 获取项目classpath路径下的所有class文件
		File classpath = new File(APIController.class.getClassLoader().getResource("").getFile());
		List<String> fileList = APIGenerator.getFiles(classpath);

		for (String classFile : fileList) {
			Class<?> c = Class.forName(APIGenerator.formatClassName(classpath.getPath(), classFile));
			// 获取类注解路径
			RequestMapping rm = c.getDeclaredAnnotation(RequestMapping.class);
			String classURL = "";
			if (rm != null) {
				classURL = rm.value()[0];
			}

			for (Method m : c.getDeclaredMethods()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("className", c.getCanonicalName());
				// 方法注解
				for (Annotation a : m.getDeclaredAnnotations()) {
					map.put("methodName", m.getName());
					if (a instanceof RequestMapping) {
						map.put("url", classURL + ((RequestMapping) a).value()[0]);
						map.put("name", ((RequestMapping) a).name());
						list.add(map);
						continue;
					} else if (a instanceof GetMapping) {
						map.put("url", classURL + ((GetMapping) a).value()[0]);
						map.put("name", ((GetMapping) a).name());
						list.add(map);
						continue;
					} else if (a instanceof PostMapping) {
						map.put("url", classURL + ((PostMapping) a).value()[0]);
						map.put("name", ((PostMapping) a).name());
						list.add(map);
						continue;
					}
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

	@RequestMapping(value = "/apiDetail", name = "获取api详情")
	public void apiDetail(@RequestParam(value = "className") String className,
			@RequestParam(value = "methodName") String methodName, HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {

		Class<?> c = Class.forName(className);
		// 获取类注解路径
		RequestMapping rm = c.getDeclaredAnnotation(RequestMapping.class);
		String classURL = "";
		if (rm != null) {
			classURL = rm.value()[0];
		}

		for (Method m : c.getDeclaredMethods()) {
			if (!m.getName().equals(methodName)) {
				continue;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			// 方法注解
			for (Annotation a : m.getDeclaredAnnotations()) {
				if (a instanceof RequestMapping) {
					map.put("url", classURL + ((RequestMapping) a).value()[0]);
					map.put("name", ((RequestMapping) a).name());
				} else if (a instanceof GetMapping) {
					map.put("url", classURL + ((GetMapping) a).value()[0]);
					map.put("name", ((GetMapping) a).name());
				} else if (a instanceof PostMapping) {
					map.put("url", classURL + ((PostMapping) a).value()[0]);
					map.put("name", ((PostMapping) a).name());
				}
			}

			// 获取入参
			List<APIParameter> parameters = new ArrayList<APIParameter>();
			for (Parameter a : m.getParameters()) {
				APIParameter parameter = new APIParameter();
				for (Annotation b : a.getAnnotations()) {
					if (b instanceof RequestParam) {
						RequestParam p = (RequestParam) b;
						parameter.setName(p.name());
						parameter.setValue(p.value() == null || p.value().trim().isEmpty() ? a.getName() : p.value());
						parameter.setRequired(p.required());
						parameter.setType(a.getType().getSimpleName());
						parameters.add(parameter);
					} else if (b instanceof PathVariable) {
						PathVariable p = (PathVariable) b;
						parameter.setName(p.name());
						parameter.setValue(p.value() == null || p.value().trim().isEmpty() ? a.getName() : p.value());
						parameter.setRequired(p.required());
						parameter.setType(a.getType().getSimpleName());
						parameters.add(parameter);
					}
				}
			}
			map.put("parameters", parameters);

			// 获取返回类型
			Class<?> returnType = m.getReturnType();

			// 获取参数关系注解
			APIResultRelation[] apiResultRelations = m.getAnnotationsByType(APIResultRelation.class);

			// 实例化返回类型
			Object o = returnType.newInstance();

			// 输出返回类型集合
			Map<String, Object> returnTypeMap = APIGenerator.parseObjectToMap(o);
			map.put("returnType", returnTypeMap);

			// 根据参数关系注解修改返回类型示例的字段
			for (APIResultRelation apiResultRelation : apiResultRelations) {
				Field field = returnType.getDeclaredField(apiResultRelation.source());
				if (field != null) {
					field.setAccessible(true);
					@SuppressWarnings("unchecked")
					Map<String, Object> fieldMap = (Map<String, Object>) returnTypeMap.get(apiResultRelation.source());
					fieldMap.put("type", apiResultRelation.target().getSimpleName());
					fieldMap.put("detail", APIGenerator.parseObjectToMap(apiResultRelation.type().newInstance()));
				}
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
}
