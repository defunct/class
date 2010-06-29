package com.goodworkalan.utility;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Associates a value to a type by either matching the a given class exactly,
 * mapping an annotation applied to a given class, or by matching the given
 * class or any of its super-classes or implemented interfaces. These three
 * different match conditions are specified independently, so that you can chose
 * to associate by class, by annotation, or by class and its descendants.
 * <p>
 * You specify an association by an exact match using the
 * {@link #exact(Class, Object) exact} method. A match by an annotation applied
 * to a class is specified using the {@link #annotated(Class, Object) annotated}
 * method. A match specified by matching any class that is assignable to a given
 * class is specified by the {@link #assignable(Class, Object) derived} method.
 * <p>
 * An exact match will take precedence over an annotation or assignment match.
 * An annotation match will take precedence over an assignment match.
 * <p>
 * This class is thread safe. All associations are stored in concurrent maps so
 * the behavior associated with concurrent maps, where a value written in one
 * thread is not yet be readable in another thread until the write is complete,
 * applies to this class.
 * <p>
 * After an association lookup, the result is cached, so that reflection and
 * class hierarchy navigation does not have to be repeated. Assigning new
 * associations resets the cache.
 * <p>
 * The concurrency model is designed to support the use of the
 * <code>ClassAssocaition</code> class as a static object. If there is a static
 * association of helper objects by type, a new class may be loaded by the class
 * loader, initialize itself in its static initialization block by registering a
 * helper object for its type, and the helper object would be ready for lookup
 * by the time an instance of newly loaded object needed its helper object.
 * 
 * @author Alan Gutierrez
 * 
 * @param <T>
 *            The type of value associated.
 */
public class ClassAssociation<T> {
    /**
     * Cache of classes to there associated types, resolved by inspecting the
     * stipulated types, and cleared when new stipulations are given.
     */
    private final ConcurrentMap<Class<?>, T> cache = new ConcurrentHashMap<Class<?>, T>();

    /** Map of assignable classes to values. */
    private final ConcurrentMap<Class<?>, T> assignable = new ConcurrentHashMap<Class<?>, T>();

    /** Map of exact classes to values. */
    private final ConcurrentMap<Class<?>, T> exact = new ConcurrentHashMap<Class<?>, T>();
    
    /** Map of class annotations to values. */
    private final ConcurrentMap<Class<? extends Annotation>, T> annotated = new ConcurrentHashMap<Class<? extends Annotation>, T>();
    
    /**
     * Create an empty class association.
     */
    public ClassAssociation() {
    }

    /**
     * Create a class association that is a copy of the given class association.
     * All of the internal state is copied, so that changes made to the copied
     * class association do not affect the new class association.
     * 
     * @param copy
     *            The class association to copy.
     */
    public ClassAssociation(ClassAssociation<T> copy) {
        exact.putAll(copy.exact);
        assignable.putAll(copy.assignable);
        annotated.putAll(copy.annotated);
    }

    /**
     * Map the given <code>value</code> to the given <code>type</code> exactly.
     * Sub-classes or implementations of the given <code>type</code> will not be
     * associated with the given <code>value</code> as a result of this
     * association.
     * <p>
     * This method is thread safe since all mappings are kept in concurrent
     * maps.
     * <p>
     * Calling this method will reset the internal cache of resolved mappings.
     * 
     * @param type
     *            The type.
     * @param value
     *            The value to associate with the type.
     */
    public void exact(Class<?> type, T value) {
        cache.clear();
        exact.put(type, value);
    }

    /**
     * Map the given <code>value</code> to the given <code>type</code>,
     * sub-classes of the given <code>type</code> or implementations of the
     * given <code>type</code> . The value will match an assignable association
     * if there is no exact association or annotation association that matches
     * the given <code>type</code> in this class association.
     * <p>
     * If a type can match multiple super-classes or interfaces, it will match
     * the first mapping or interface association encountered when inspecting
     * the class and super classes of given type. The class is first tested,
     * then each super-class of the class it tested, from the class itself up
     * the inheritance hierarchy to <code>Object</code>. For each class in the
     * hierarchy, all of the interfaces implemented by the class and all of the
     * interfaces that those interfaces extend are checked for a derived
     * association. The first derived association to match is returned.
     * <p>
     * If a class directly implements or an interface directly extends two or
     * more interfaces that have a derived association, there is no telling
     * which of the two or more associations will be chosen. Otherwise, you can
     * determine which derived association will be chosen by working your way up
     * the class and interface hierarchy according to the rules.
     * 
     * @param type
     *            The object type.
     * @param value
     *            The value to associate with the type.
     */
    public void assignable(Class<?> type, T value) {
        cache.clear();
        assignable.put(type, value);
    }

    /**
     * Map the given <code>value</code> to classes annotated with the given
     * <code>annotation</code> type. The value will match a annotated
     * association if there is no exact association that matches the given type.
     * If a type is annotated by two ore more annotations that have associated
     * values, their is no telling which of the values will be returned.
     * <p>
     * Annotations applied to super-classes or interfaces implemented by the
     * type will not be considered when determining the match.
     * 
     * @param annotation
     *            The annotation type.
     * @param value
     *            The value to associate with the annotation type.
     */
    public void annotated(Class<? extends Annotation> annotation, T value) {
        cache.clear();
        annotated.put(annotation, value);
    }

    /**
     * Scans each of the interfaces in the given array of interfaces and each of
     * their super-interfaces for an associated value in the map of derived
     * associations, returning the first association encountered or null if none
     * is found.
     * 
     * @param interfaces
     *            An array of interfaces to check for associated values.
     * @return A value associated with one of the interfaces or its
     *         super-interfaces or null.
     */
    private T byInterface(Class<?>[] interfaces) {
        LinkedList<Class<?>> queue = new LinkedList<Class<?>>(Arrays.asList(interfaces));
        while (!queue.isEmpty()) {
            Class<?> iface = queue.removeFirst();
            T value = assignable.get(iface);
            if (value != null) {
                return value;
            }
            queue.addAll(Arrays.asList((Class<?>[]) iface.getInterfaces()));
        }
        return null;
    }

    /*
     * Note that it would be trivial to add the ability to search for interfaces
     * with a particular annotation, but it doesn't seem to be a particularly
     * common use case. In fact, I'm not sure that one should be able to match a
     * base class, if you are going to match a base class, why not an interface
     * so annotated as well?
     */

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
                for (Map.Entry<Class<? extends Annotation>, T> entry : annotated.entrySet()) {
                    if (type.isAnnotationPresent(entry.getKey())) {
                        value = entry.getValue();
                        break;
                    }
                }
            }
            if (value == null) {
                Class<?> iterator = type;
                while (iterator != null) {
                    value = assignable.get(iterator);
                    if (value == null) {
                        value = byInterface(iterator.getInterfaces());
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
