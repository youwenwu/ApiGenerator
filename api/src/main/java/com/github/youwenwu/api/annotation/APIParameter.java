package com.github.youwenwu.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 定义API入参
 * @author youwenwu
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface APIParameter {
	/**
	 * 基本类型参数
	 * @return
	 */
	
	APIField[] parameters();
}
