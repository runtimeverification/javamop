package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test ERE repeating elements.
 * @author A. Cody Schuffelen
 */
public class RepeatTest {
    
    private Symbol a;
    private ERE a_x3;
    private ERE a_x5;
    
    @Before
    public void setUp() {
        a = Symbol.get("a");
        a_x5 = Repeat.get(a, 5);
        a_x3 = Repeat.get(a, 3);
    }
    
    /**
     * Test that two equivalent repeat elements are equal.
     */
    @Test
    public void equalityTest() {
        ERE a_x5_again = Repeat.get(a, 5);
        
        assertEquals(a_x5, a_x5_again);
        assertEquals(0, a_x5.compareTo(a_x5_again));
    }
    
    /**
     * Test two different repeat elements are inequal.
     */
    @Test
    public void inequalityTest() {
        ERE a_x10 = Repeat.get(a, 10);
        Symbol b = Symbol.get("b");
        ERE b_x5 = Repeat.get(b, 5);
        
        assertFalse(a_x5.equals(a_x10));
        assertFalse(0 == a_x5.compareTo(a_x10));
        assertFalse(a_x5.equals(b_x5));
        assertFalse(0 == a_x5.compareTo(b_x5));
    }
    
    /**
     * Test that repeat elements derive the repeated element the required number of times, and not other elements.
     */
    @Test
    public void deriveTest() {
        Symbol a = Symbol.get("a");
        Epsilon epsilon = Epsilon.get();
        
        assertEquals(epsilon, a_x3.derive(a).derive(a).derive(a));
        
        Empty empty = Empty.get();
        assertEquals(empty, a_x3.derive(a).derive(a).derive(a).derive(a));
        
        Symbol b = Symbol.get("b");
        assertEquals(empty, a_x3.derive(b));
    }
    
    /**
     * Test that repeat elements do not contain epsilons.
     */
    @Test
    public void containsEpsilonTest() {
        assertFalse(a_x3.containsEpsilon());
    }
    
    /**
     * Test that copied repeat elements compare equal.
     */
    @Test
    public void testCopy() {
        ERE copy = a_x3.copy();
        assertEquals(a_x3, copy);
    }
}