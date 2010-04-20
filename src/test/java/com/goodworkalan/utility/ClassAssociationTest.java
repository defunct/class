package com.goodworkalan.utility;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link ClassAssociation}.
 * 
 * @author Alan Gutierrez
 */
public class ClassAssociationTest {
    /** Test matching a class based on an annotation. */
    @Test
    public void annotated() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.annotated(Three.class, "Three");
        
        assertNull(association.get(One.class));
        assertNull(association.get(One.class));
        assertEquals(association.get(Two.class), "Three");
        assertEquals(association.get(Two.class), "Three");
     
        association = new ClassAssociation<String>();
        association.annotated(Override.class, "Ignore");
        assertNull(association.get(Two.class));
    }
    
    /** Test matching a class exactly. */
    @Test
    public void exact() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.exact(Two.class, "Two");
        
        assertNull(association.get(One.class));
        assertNull(association.get(One.class));
        assertEquals(association.get(Two.class), "Two");
        assertEquals(association.get(Two.class), "Two");
    }
    
    /** Test matching a class by super-classes or interfaces. */
    @Test
    public void derived() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.assignable(Four.class, "Four");
        association.assignable(Two.class, "Two");
        
        assertNull(association.get(One.class));
        assertNull(association.get(One.class));
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(Two.class), "Two");
        assertNull(association.get(Six.class));
    }
 
    /** Test the copy constructor. */
    @Test
    public void copy() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.assignable(Four.class, "Four");
        association.exact(One.class, "One");
        association.annotated(Three.class, "Three");

        association = new ClassAssociation<String>(association);
        
        assertEquals(association.get(Two.class), "Three");
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(One.class), "One");
        assertNull(association.get(Six.class));
    }
    
    /** Test lookup of an array class. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void array() {
        new ClassAssociation<String>().get(new Object[0].getClass());
    }

    /** Test lookup of a primitive class. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void primitive() {
        new ClassAssociation<String>().get(int.class);
    }
}
