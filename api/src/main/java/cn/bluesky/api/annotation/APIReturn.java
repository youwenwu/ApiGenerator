package cn.bluesky.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 描述接口返回值
 * @author youwenwu
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface APIReturn {
	/**
	 * 返回类型，例如List, Map等等
	 * @return
	 */
	String type() default "";
	/**
	 * 定义接口返回的数据结构
	 * @return
	 */
	APIField[] fields();
	
	
}
