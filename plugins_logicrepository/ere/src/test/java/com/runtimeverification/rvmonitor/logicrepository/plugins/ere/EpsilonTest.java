package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Epsilon or empty string ERE class.
 * @author A. Cody Schuffelen
 */
public class EpsilonTest {
    
    private Epsilon epsilon;
    
    @Before
    public void setUp() {
        epsilon = Epsilon.get();
    }
    
    /**
     * Test two instances of the class retrieved through the standard method are equal.
     */
    @Test
    public void testEquality() {
        Epsilon two = Epsilon.get();
        assertEquals(epsilon, two);
        assertEquals(0, epsilon.compareTo(two));
    }
    
    /**
     * Test Epsilon doesn't compare equal with other classes.
     */
    @Test
    public void testInequality() {
        Empty two = Empty.get();
        assertFalse(epsilon.equals(two));
        assertFalse(0 == epsilon.compareTo(two));
    }
    
    /**
     * Test the EREType of the Epsilon element is correct.
     */
    @Test
    public void testType() {
        assertEquals(epsilon.getEREType(), EREType.EPS);
    }
    
    /**
     * Ensure the Epsilon instances convert to the expected string.
     */
    @Test
    public void testString() {
        assertEquals("epsilon", epsilon.toString());
    }
    
    /**
     * Ensure that Epsilon instances derive to empty instances.
     */
    @Test
    public void testDerive() {
        ERE derived = epsilon.derive(Symbol.get("test"));
        Empty empty = Empty.get();
        assertEquals(empty, derived);
    }
    
    /**
     * Ensure that Epsilon instances contain epsilons.
     */
    @Test
    public void testContainsEpsilon() {
        assertTrue(epsilon.containsEpsilon());
    }
    
    /**
     * Ensure Empty instances copy to themselves.
     */
    @Test
    public void testCopy() {
        ERE copy = epsilon.copy();
        assertEquals(epsilon, copy);
    }
}