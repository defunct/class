package com.goodworkalan.utility;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

public class ClassAssociationTest {
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
    
    @Test
    public void exact() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.exact(Two.class, "Two");
        
        assertNull(association.get(One.class));
        assertNull(association.get(One.class));
        assertEquals(association.get(Two.class), "Two");
        assertEquals(association.get(Two.class), "Two");
    }
    
    @Test
    public void derived() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.derived(Four.class, "Four");
        association.derived(Two.class, "Two");
        
        assertNull(association.get(One.class));
        assertNull(association.get(One.class));
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(Two.class), "Two");
        assertNull(association.get(Six.class));
    }
 
    @Test
    public void copy() {
        ClassAssociation<String> association = new ClassAssociation<String>();
        association.derived(Four.class, "Four");
        association.exact(One.class, "One");
        association.annotated(Three.class, "Three");

        association = new ClassAssociation<String>(association);
        
        assertEquals(association.get(Two.class), "Three");
        assertEquals(association.get(Five.class), "Four");
        assertEquals(association.get(One.class), "One");
        assertNull(association.get(Six.class));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void array() {
        new ClassAssociation<String>().get(new Object[0].getClass());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void primitive() {
        new ClassAssociation<String>().get(int.class);
    }
}
