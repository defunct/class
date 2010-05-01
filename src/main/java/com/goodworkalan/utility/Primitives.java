package com.goodworkalan.utility;

/**
 * Container for a static utility method that converts a primitive Java class to
 * its corresponding Object based Java class.
 * 
 * @author Alan Gutierrez
 */
public class Primitives {
    /**
     * This is a container for static utility methods and is not meant to be
     * instantiated.
     */
    Primitives() {
    }

    /**
     * If the given type is a Java primitive return the associated
     * <code>java.lang.Object</code> based class, otherwise return the given
     * type.
     * 
     * @param type
     *            The type to convert to a <code>java.lang.Object</code> derived
     *            class.
     * @return An <code>java.lang.Object<code> derived class.
     */
    public static Class<?> box(Class<?> type) {
        if (type.isPrimitive()) {
            if (long.class.isAssignableFrom(type)) {
                return Long.class;
            } else if (int.class.isAssignableFrom(type)) {
                return Integer.class;
            } else if (short.class.isAssignableFrom(type)) {
                return Short.class;
            } else if (char.class.isAssignableFrom(type)) {
                return Character.class;
            } else if (byte.class.isAssignableFrom(type)) {
                return Byte.class;
            } else if (boolean.class.isAssignableFrom(type)) {
                return Boolean.class;
            } else if (float.class.isAssignableFrom(type)) {
                return Float.class;
            } else if (void.class.isAssignableFrom(type)) {
                return Void.class;
            } else /* if (double.class.isAssignableFrom(to)) */ {
                return Double.class;
            }
        }
        return type;
    }
}
