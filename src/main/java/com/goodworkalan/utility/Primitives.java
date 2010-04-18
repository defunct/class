package com.goodworkalan.utility;

public class Primitives {
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
