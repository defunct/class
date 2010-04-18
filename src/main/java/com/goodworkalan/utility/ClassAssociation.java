package com.goodworkalan.utility;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassAssociation<T> {
    /**
     * The classes to their object diffusers as resolved by ascending the object
     * hierarchy, looking for an object diffuser that will diffuse a super class
     * or interface. This cache is reset when a new object diffuser is assigned
     * using the {@link #setConverter(Class, ObjectDiffuser) setConverter}
     * method.
     */
    private final ConcurrentMap<Class<?>, T> cache = new ConcurrentHashMap<Class<?>, T>();

    /** Map of assigned classes to object diffusers. */
    private final ConcurrentMap<Class<?>, T> derived = new ConcurrentHashMap<Class<?>, T>();

    /** Map of assigned classes to object diffusers. */
    private final ConcurrentMap<Class<?>, T> exact = new ConcurrentHashMap<Class<?>, T>();
    
    private final ConcurrentMap<Class<? extends Annotation>, T> annotated = new ConcurrentHashMap<Class<? extends Annotation>, T>();
    
    public ClassAssociation() {
    }
    
    public ClassAssociation(ClassAssociation<T> copy) {
        exact.putAll(copy.exact);
        derived.putAll(copy.derived);
        annotated.putAll(copy.annotated);
    }

    public void exact(Class<?> type, T value) {
        cache.clear();
        exact.put(type, value);
    }

    /**
     * Assign the given object converter to the given object type. The converter
     * will be assigned to a map of converters that is associated with the
     * <code>ClassLoader</code> of the given object type. The assignment will be
     * inherited by any subsequently created child class loaders of the
     * associated class loader, but not by existing child class loaders.
     * 
     * @param type
     *            The object type.
     * @param converter
     *            The object converter.
     */
    public void derived(Class<?> type, T value) {
        cache.clear();
        derived.put(type, value);
    }
    
    public void annotated(Class<? extends Annotation> annotation, T value) {
        cache.clear();
        annotated.put(annotation, value);
    }

    private T byInterface(Class<?>[] ifaces) {
        LinkedList<Class<?>> queue = new LinkedList<Class<?>>(Arrays.asList(ifaces));
        while (!queue.isEmpty()) {
            Class<?> iface = queue.removeFirst();
            T value = derived.get(iface);
            if (value != null) {
                return value;
            }
            queue.addAll(Arrays.asList((Class<?>[]) iface.getInterfaces()));
        }
        return null;
    }
    
    private T byAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            for (Map.Entry<Class<? extends Annotation>, T> entry : annotated.entrySet()) {
                if (entry.getKey().isAssignableFrom(annotation.getClass())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Get the object converter for the given object type.
     * 
     * @param type
     *            The object type.
     * @return The object converter.
     */
    public T get(Class<?> type) {
        T value = cache.get(type);
        if (value == null) {
            if (type.isArray()) {
                throw new IllegalArgumentException();
            }
            if (type.isPrimitive()) {
                throw new IllegalArgumentException();
            }
            value = exact.get(type);
            if (value == null) {
                Class<?> iterator = type;
                while (iterator != null) {
                    value = derived.get(iterator);
                    if (value == null) {
                        value = byInterface(iterator.getInterfaces());
                    }
                    if (value == null) {
                        value = byAnnotation(iterator.getAnnotations());
                    }
                    if (value != null) {
                        cache.put(type, value);
                        break;
                    }
                    iterator = iterator.getSuperclass();
                }
            } else {
                cache.put(type, value);
            }
        }
        return value;
    }
}
