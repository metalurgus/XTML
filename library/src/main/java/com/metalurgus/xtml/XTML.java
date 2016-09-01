package com.metalurgus.xtml;

import com.metalurgus.xtml.annotation.XTMLMapping;

import org.jsoup.nodes.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vladislav Matvienko
 */
public class XTML {
    public static <T> T fromHTML(Element element, Class<T> classOfT) {
        T result = null;

        Map<Field, XTMLMapping> fields = new HashMap<>();
        for (Field field : classOfT.getFields()) {
            for (Annotation a : field.getDeclaredAnnotations()) {
                if (a instanceof XTMLMapping) {
                    fields.put(field, (XTMLMapping) a);
                    break;
                }
            }
        }


        return result;
    }
}
