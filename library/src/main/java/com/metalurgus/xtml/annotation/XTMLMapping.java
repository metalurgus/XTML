package com.metalurgus.xtml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vladislav Matvienko
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XTMLMapping {
    Type type();
    String name() default "";
    int index() default -1;
    String select() default "";
    String id() default "";
    String mappingName() default "";




    enum Type {
        TAG, ATTRIBUTE, COLLECTION, HTML
    }
}
