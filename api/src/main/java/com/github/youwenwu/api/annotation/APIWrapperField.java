package com.github.youwenwu.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Repeatable(value = APIWrapperFields.class)
public @interface APIWrapperField {
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
