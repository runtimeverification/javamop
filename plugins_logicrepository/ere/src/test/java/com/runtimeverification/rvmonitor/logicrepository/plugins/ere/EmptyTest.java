package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Empty ERE class.
 * @author A. Cody Schuffelen
 */
public class EmptyTest {
    
    private Empty empty;
    
    @Before
    public void setUp() {
        empty = Empty.get();
    }
    
    /**
     * Test two instances of the class retrieved through the standard method are equal.
     */
    @Test
    public void testEquality() {
        Empty two = Empty.get();
        assertEquals(empty, two);
        assertEquals(0, empty.compareTo(two));
    }
    
    /**
     * Test Empty doesn't compare equal with other classes.
     */
    @Test
    public void testInequality() {
        Epsilon two = Epsilon.get();
        assertFalse(empty.equals(two));
        assertFalse(0 == empty.compareTo(two));
    }
    
    /**
     * Test the EREType of the Empty element is correct.
     */
    @Test
    public void testType() {
        assertEquals(empty.getEREType(), EREType.EMP);
    }
    
    /**
     * Ensure the Empty instances convert to the expected string.
     */
    @Test
    public void testString() {
        assertEquals("empty", empty.toString());
    }
    
    /**
     * Ensure that Empty instances derive to themselves.
     */
    @Test
    public void testDerive() {
        ERE derived = empty.derive(Symbol.get("test"));
        assertEquals(empty, derived);
    }
    
    /**
     * Ensure that Empty instances don't contain epsilons.
     */
    @Test
    public void testContainsEpsilon() {
        assertFalse(empty.containsEpsilon());
    }
    
    /**
     * Ensure Empty instances copy to themselves.
     */
    @Test
    public void testCopy() {
        ERE copy = empty.copy();
        assertEquals(empty, copy);
    }
}