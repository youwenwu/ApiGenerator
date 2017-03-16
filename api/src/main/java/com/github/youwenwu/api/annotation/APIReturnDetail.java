package com.github.youwenwu.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(value = APIReturnDetails.class)
public @interface APIReturnDetail {
	/**
	 * 参照字段标识
	 * @return
	 */
	String id();
	
	/**
	 * 数据结构
	 * @return
	 */
	APIField[] fields();
}
