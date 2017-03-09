package cn.bluesky.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface APIField {
	/**
	 * 字段名称
	 * @return
	 */
	String name() default "";
	/**
	 * 类型
	 * @return
	 */
	String type() default "";
	/**
	 * 字段描述
	 * @return
	 */
	String desc() default "";
	/**
	 * 是否可为空
	 * @return
	 */
	boolean required() default false;
	/**
	 * 详细数据结构参照标识(如果是包装类型用到)
	 * @return
	 */
	String refId() default "";
	/**
	 * 示例
	 * @return
	 */
	String example() default "";
}
