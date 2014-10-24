package com.JavaPersistence.Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * po类型的注解,对vo类包含几个Po类进行标注
 * @author AntsMarch
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PoClassAnnotation {
		public String Class() default "";
}
