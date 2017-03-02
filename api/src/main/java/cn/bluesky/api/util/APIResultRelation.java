package cn.bluesky.api.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(value = APIResultRelations.class)
public @interface APIResultRelation {
	String source();
	Class<?> target();
	Class<?> type();
}
