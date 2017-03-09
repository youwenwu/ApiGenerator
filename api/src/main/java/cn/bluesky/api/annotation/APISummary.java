package cn.bluesky.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface APISummary {
	/**
	 * 名称
	 * @return
	 */
	String name() default "";
	/**
	 * 请求地址
	 * @return
	 */
	String url() default "";
	/**
	 * 请求类型, GET/POST
	 * @return
	 */
	String methodType() default "GET/POST";
}
