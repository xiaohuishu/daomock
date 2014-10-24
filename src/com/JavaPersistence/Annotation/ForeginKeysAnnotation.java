package com.JavaPersistence.Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外键注解,标注外键的一些属性
 * 
 * @author AntsMarch
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeginKeysAnnotation {
	public String column() default "";

	public String type() default "";

	public String key() default "foreign";

}
