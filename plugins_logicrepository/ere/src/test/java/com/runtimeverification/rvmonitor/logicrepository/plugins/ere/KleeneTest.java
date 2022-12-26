package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Kleene Star ERE operator.
 * @author A. Cody Schuffelen
 */
public class KleeneTest {
    
    private Symbol a;
    private Symbol b;
    private ERE aStar;
    private ERE bStar;
    
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        aStar = Kleene.get(a);
        bStar = Kleene.get(b);
    }
    
    /**
     * Test that equivalent Kleene operators compare equal.
     */
    @Test
    public void testEquality() {
        ERE aStar_again = Kleene.get(a);
        
        assertEquals(aStar, aStar_again);
        assertEquals(0, aStar.compareTo(aStar_again));
    }
    
    /**
     * Test that different Kleene operators compare inequal, and Kleene operators compare inequal with other operators.
     */
    @Test
    public void testInequality() {
        assertFalse(aStar.equals(bStar));
        assertFalse(0 == aStar.compareTo(bStar));
        assertFalse(aStar.equals(a));
        assertFalse(0 == aStar.compareTo(a));
    }
    
    /**
     * Test that Kleene EREs have the correct EREType.
     */
    @Test
    public void testEREType() {
        assertEquals(EREType.STAR, aStar.getEREType());
    }
    
    /**
     * Test that Kleene operators convert to strings properly.
     */
    @Test
    public void testString() {
        assertEquals("a*", aStar.toString());
    }
    
    /**
     * Test that Kleene stars contain epsilons.
     */
    @Test
    public void testContainsEpsilon() {
        assertTrue(aStar.containsEpsilon());
    }
    
    /**
     * Test that copying Kleene elements produces equivalent ones.
     */
    @Test
    public void testCopy() {
        ERE copy = aStar.copy();
        assertEquals(aStar, copy);
    }
    
    /**
     * Test that Kleene elements derive correctly.
     */
    @Test
    public void testDerive() {
        Epsilon epsilon = Epsilon.get();
        Empty empty = Empty.get();
        
        assertEquals(aStar, aStar.derive(a));
        assertEquals(empty, aStar.derive(b));
        
        ERE ab = Concat.get(a, b);
        ERE ab_Star = Kleene.get(ab);
        assertEquals(ab_Star, ab_Star.derive(a).derive(b));
        assertEquals(empty, ab_Star.derive(b));
    }
}