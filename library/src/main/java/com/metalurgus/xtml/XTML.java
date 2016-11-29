package com.metalurgus.xtml;

import android.text.TextUtils;

import com.github.drapostolos.typeparser.TypeParser;
import com.metalurgus.xtml.annotation.XTMLClass;
import com.metalurgus.xtml.annotation.XTMLMapping;
import com.metalurgus.xtml.annotation.XTMLMappings;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Vladislav Matvienko
 */
public class XTML {

    static TypeParser parser = TypeParser.newBuilder().build();

    public static <T> T fromHTML(Element element, String mappingName, Class<T> classOfT) {
        return fromHTML(element, mappingName, classOfT, null);
    }

    private static <T> T fromHTML(Element element, String mappingName, Class<T> classOfT, Object constructorParameter) {
        if (!classOfT.isAnnotationPresent(XTMLClass.class)) {
            throw new IllegalArgumentException("Target class is not annotated with @XTMLClass annotation");
        }
        Constructor<T> constructor;
        try {

            constructor = classOfT.getConstructor();
        } catch (NoSuchMethodException e) {
            try {
                constructor = classOfT.getDeclaredConstructor(constructorParameter.getClass());
                constructor.setAccessible(true);
            } catch (NoSuchMethodException e1) {
                throw new IllegalArgumentException("Target class has no default constructor (with no arguments)", e);
            }
        }
        T result = null;
        try {
            if (constructor.getParameterTypes().length == 0) {
                result = constructor.newInstance();
            } else {
                result = constructor.newInstance(constructorParameter);
            }
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Failed to instantiate a target class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Default constructor of target class is not accessible", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate a target class", e);
        }

        //find all fields marked with @XTMLMapping annotation
        Map<Field, XTMLMapping> fields = new HashMap<>();
        for (Field field : classOfT.getDeclaredFields()) {
            processField(field, fields, mappingName);
        }

        //write all fields to object
        for (Field field : fields.keySet()) {
            field.setAccessible(true);
            XTMLMapping mapping = fields.get(field);
            switch (mapping.type()) {
                case TAG:
                    //convert the whole element to the field
                    Element targetElement = selectElementForMapping(element, mapping);
                    if (field.getType().isAnnotationPresent(XTMLClass.class)) {
                        try {
                            field.set(result, fromHTML(targetElement, mappingName, field.getType(), result));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            //TODO: do something in this case later
                        }
                    } else {
                        try {
                            field.set(result, parser.parse(targetElement.ownText(), field.getType()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            //TODO: do something in this case later
                        }
                    }
                    break;
                case ATTRIBUTE:
                    //write attribute value to a field
                    String attribute = null;
                    try {
                        if (!TextUtils.isEmpty(mapping.select())) {
                            if (mapping.index() >= 0) {
                                attribute = element.select(mapping.select()).get(mapping.index()).attr(mapping.name());
                            } else {
                                attribute = element.select(mapping.select()).get(0).attr(mapping.name());
                            }
                        } else if (mapping.index() >= 0) {
                            attribute = element.child(mapping.index()).attr(mapping.name());
                        } else {
                            attribute = element.attr(mapping.name());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        field.set(result, parser.parse(attribute, field.getType()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //TODO: do something in this case later
                    }
                    break;
                case COLLECTION:
                    //write a set of nodes to a collection
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        if (!TextUtils.isEmpty(mapping.select())) {
                            Collection collection = null;
                            if (Modifier.isAbstract(field.getType().getModifiers())) {
                                collection = createConcreteObject(field.getType());
                            } else {
                                try {
                                    collection = (Collection) field.getType().newInstance();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (collection == null) {
                                return null;
                            } else {
                                ParameterizedType genericMemberType = (ParameterizedType) field.getGenericType();
                                Class<?> memberType = (Class<?>) genericMemberType.getActualTypeArguments()[0];

                                Elements elements = element.select(mapping.select());
                                for (Element e : elements) {
                                    if (memberType.isAnnotationPresent(XTMLClass.class)) {
                                        try {
                                            collection.add(fromHTML(e, mapping.mappingName(), memberType, result));
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            //TODO: do something in this case later
                                        }
                                    } else {
                                        try {
                                            collection.add(parser.parse(e.ownText(), memberType));
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            //TODO: do something in this case later
                                        }
                                    }
                                }
                            }

                            try {
                                field.set(result, collection);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                //TODO: do something in this case later
                            }
                        }
                    }
                    break;
                case HTML:
                    targetElement = selectElementForMapping(element, mapping);
                    try {
                        field.set(result, parser.parse(targetElement.html(), field.getType()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //TODO: do something in this case later
                    }
                    break;
            }
        }


        return result;
    }

    private static void processField(Field field, Map<Field, XTMLMapping> fields, String mappingName) {
        if (field.isAnnotationPresent(XTMLMapping.class) || field.isAnnotationPresent(XTMLMappings.class)) {
            for (Annotation a : field.getDeclaredAnnotations()) {
                if (a instanceof XTMLMapping) {
                    if (TextUtils.isEmpty(((XTMLMapping) a).mappingName()) || TextUtils.isEmpty(mappingName)) {
                        fields.put(field, (XTMLMapping) a);
                        break;
                    }
                    if (mappingName.equals(((XTMLMapping) a).mappingName())) {
                        fields.put(field, (XTMLMapping) a);
                        break;
                    }
                } else {
                    if (a instanceof XTMLMappings) {
                        XTMLMappings mappings = (XTMLMappings) a;
                        for (XTMLMapping mapping : mappings.value()) {
                            if (TextUtils.isEmpty(mapping.mappingName()) && TextUtils.isEmpty(mappingName)) {
                                fields.put(field, mapping);
                                break;
                            }
                            if (mappingName.equals(mapping.mappingName())) {
                                fields.put(field, mapping);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (field.isAnnotationPresent(XTMLMappings.class)) {
            XTMLMappings mappings = null;

        }
    }

    private static Element selectElementForMapping(Element element, XTMLMapping mapping) {
        try {
            if (!TextUtils.isEmpty(mapping.id())) {
                return element.getElementById(mapping.id());
            } else if (!TextUtils.isEmpty(mapping.select())) {
                Elements elements = element.select(mapping.select());
                if (mapping.index() >= 0) {
                    return elements.get(mapping.index());
                } else {
                    return elements.get(0);
                }
            } else if (mapping.index() >= 0) {
                return element.child(mapping.index());
            } else if (!TextUtils.isEmpty(mapping.name())) {
                return element.select("[name=" + mapping.name() + "]").get(0);
            }
            return element;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Collection createConcreteObject(Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList();
        } else if (Set.class.isAssignableFrom(type)) {
            return new HashSet();
        } else if (Queue.class.isAssignableFrom(type)) {
            return new ArrayDeque();
        } else return null;
    }

}
