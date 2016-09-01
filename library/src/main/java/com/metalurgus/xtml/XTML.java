package com.metalurgus.xtml;

import android.text.TextUtils;

import com.github.drapostolos.typeparser.TypeParser;
import com.metalurgus.xtml.annotation.XTMLClass;
import com.metalurgus.xtml.annotation.XTMLMapping;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public static <T> T fromHTML(Element element, Class<T> classOfT) {
        return fromHTML(element, classOfT, null);
    }

    private static <T> T fromHTML(Element element, Class<T> classOfT, Object constructorParameter) {
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
        for (Field field : classOfT.getFields()) {
            if (field.isAnnotationPresent(XTMLMapping.class)) {
                fields.put(field, field.getAnnotation(XTMLMapping.class));
            }
        }

        //write all fields to object
        for (Field field : fields.keySet()) {
            XTMLMapping mapping = fields.get(field);
            switch (mapping.type()) {
                case TAG:
                    if (field.getType().isAnnotationPresent(XTMLClass.class)) {
                        if (!TextUtils.isEmpty(mapping.id())) {
                            try {
                                field.set(result, fromHTML(element.getElementById(mapping.id()), field.getType(), result));
                            } catch (IllegalAccessException e) {
                                //TODO: do something in this case later
                            }
                        } else if (!TextUtils.isEmpty(mapping.select())) {
                            Elements elements = element.select(mapping.select());
                            if (mapping.index() >= 0) {
                                try {
                                    field.set(result, fromHTML(elements.get(mapping.index()), field.getType(), result));
                                } catch (IllegalAccessException e) {
                                    //TODO: do something in this case later
                                }
                            } else {
                                try {
                                    field.set(result, fromHTML(elements.get(0), field.getType(), result));
                                } catch (IllegalAccessException e) {
                                    //TODO: do something in this case later
                                }
                            }
                        } else if (mapping.index() >= 0) {
                            try {
                                field.set(result, fromHTML(element.child(mapping.index()), field.getType(), result));
                            } catch (IllegalAccessException e) {
                                //TODO: do something in this case later
                            }
                        } else if (!TextUtils.isEmpty(mapping.name())) {
                            try {
                                field.set(result, fromHTML(element.select("[name=" + mapping.name() + "]").get(0), field.getType(), result));
                            } catch (IllegalAccessException e) {
                                //TODO: do something in this case later
                            }
                        }
                    }
                    break;
                case ATTRIBUTE:
                    String attribute = element.attr(mapping.name());
                    try {
                        field.set(result, parser.parse(attribute, field.getType()));
                    } catch (IllegalAccessException e) {
                        //TODO: do something in this case later
                    }
                    break;
                case COLLECTION:
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
                                        collection.add(fromHTML(e, memberType, result));
                                    } else {
                                        collection.add(parser.parse(e.ownText(), memberType));
                                    }
                                }


                            }

                            if(collection != null) {
                                try {
                                    field.set(result, collection);
                                } catch (IllegalAccessException e) {
                                    //TODO: do something in this case later
                                }
                            }
                        }
                    }
                    break;
            }
        }


        return result;
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
