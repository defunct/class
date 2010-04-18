package com.goodworkalan.utility;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
public class PrimitivesTest {
    /** Pointless test to get 100% test coverage. */
    @Test
    public void constructor() {
        new Primitives();
    }

    @Test
    public void box() {
        assertEquals(Void.class, Primitives.box(void.class));
        assertEquals(Boolean.class, Primitives.box(boolean.class));
        assertEquals(Byte.class, Primitives.box(byte.class));
        assertEquals(Character.class, Primitives.box(char.class));
        assertEquals(Short.class, Primitives.box(short.class));
        assertEquals(Integer.class, Primitives.box(int.class));
        assertEquals(Long.class, Primitives.box(long.class));
        assertEquals(Float.class, Primitives.box(float.class));
        assertEquals(Double.class, Primitives.box(double.class));
        assertEquals(Object.class, Primitives.box(Object.class));
    }
}
