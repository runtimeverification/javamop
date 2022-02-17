package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.Arrays;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Or ERE element.
 */
public class OrTest {
    
    private Symbol a;
    private Symbol b;
    private ERE aOrb;
    
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        aOrb = Or.get(new ArrayList<ERE>(Arrays.asList(a, b)));
    }
    
    /**
     * Test that equivalent Or elements compare equal.
     */
    @Test
    public void testEquality() {
        
        ERE or_again = Or.get(new ArrayList<ERE>(Arrays.asList(a, b)));
        ERE or_reverse = Or.get(new ArrayList<ERE>(Arrays.asList(a, b)));
        
        assertEquals(aOrb, or_again);
        assertEquals(0, aOrb.compareTo(or_again));
        assertEquals(aOrb, or_reverse);
        assertEquals(0, aOrb.compareTo(or_reverse));
    }
    
    /**
     * Test that different Or elements compare inequal.
     */
    @Test
    public void testInequality() {
        Symbol c = Symbol.get("c");
        ERE aOrc = Or.get(new ArrayList<ERE>(Arrays.asList(a, c)));
        
        assertFalse(aOrb.equals(aOrc));
        assertFalse(0 == aOrb.compareTo(aOrc));
        assertFalse(aOrb.equals(a));
        assertFalse(0 == aOrb.compareTo(a));
    }
    
    /**
     * Test that copied Or elements compare equal.
     */
    @Test
    public void testCopy() {
        ERE copy = aOrb.copy();
        assertEquals(aOrb, copy);
    }
    
    /**
     * Test that Or elements have the correct EREType.
     */
    @Test
    public void testEREType() {
        assertEquals(EREType.OR, aOrb.getEREType());
    }
    
    /**
     * Test that Or elements produce the correct strings.
     */
    @Test
    public void testString() {
        assertEquals("(a | b)", aOrb.toString());
        
        Symbol c = Symbol.get("c");
        ERE aOrbOrc = Or.get(new ArrayList<ERE>(Arrays.asList(a, b, c)));
        assertEquals("(a | b | c)", aOrbOrc.toString());
    }
    
    /**
     * Test that Or elements contain an epsilon if they have a member that contains an epsilon.
     */
    @Test
    public void testContainsEpsilon() {
        assertFalse(aOrb.containsEpsilon());
        
        Epsilon epsilon = Epsilon.get();
        ERE aOrepsilon = Or.get(new ArrayList<ERE>(Arrays.asList(a, epsilon)));
        assertTrue(aOrepsilon.containsEpsilon());
    }
    
    /**
     * Test that Or elements can derive to any of the members they contain and not anything else.
     */
    @Test
    public void testDerive() {
        Symbol c = Symbol.get("c");
        Epsilon epsilon = Epsilon.get();
        Empty empty = Empty.get();
        
        assertEquals(epsilon, aOrb.derive(a));
        assertEquals(epsilon, aOrb.derive(b));
        assertEquals(empty, aOrb.derive(c));
    }
    
    /**
     * Test that Or elements are flattened and simplified on creation.
     */
    @Test
    public void testSimplify() {
        Symbol c = Symbol.get("c");
        Epsilon epsilon = Epsilon.get();
        
        assertEquals(aOrb, Or.get(new ArrayList<ERE>(Arrays.asList(a, b, a, b, a, b, a, b, a))));
        assertEquals(Or.get(new ArrayList<ERE>(Arrays.asList(a, b, c))), Or.get(new ArrayList<ERE>(Arrays.asList(aOrb, c))));
    }
}