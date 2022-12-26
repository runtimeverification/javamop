package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the Atom class, representing symbols in the LTL expression.
 */
public class AtomTest {
    
    private Atom a;
    private Atom b;
    
    /**
     * Pre-test initialization members.
     */
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
    }
    
    /**
     * Test that Atoms constructed the same way are equal, and ones constructed different ways
     * are not equal.
     */
    @Test
    public void testEquality() {
        Atom a_again = Atom.get("a");
        
        assertEquals(a, a);
        assertEquals(a, a_again);
        assertFalse(a.equals(b));
        assertFalse(a_again.equals(b));
        
        assertEquals(a.hashCode(), a_again.hashCode());
        assertEquals(0, a.compareTo(a_again));
        assertFalse(0 == a.compareTo(b));
    }
    
    /**
     * Test that Atoms can be copied to equal elements.
     */
    @Test
    public void testCopy() {
        LTLFormula a_copy = a.copy();
        
        assertEquals(a, a_copy);
        assertEquals(0, a.compareTo(a_copy));
        assertEquals(a.hashCode(), a_copy.hashCode());
    }
    
    /**
     * Test that Atoms don't change meaning when simplified.
     */
    @Test
    public void testSimplify() {
        assertEquals(a, a.reduce());
        assertEquals(a, a.lower());
        assertEquals(a, a.normalize(false));
        assertEquals(new Negation(a), a.normalize(true));
    }
    
    /**
     * Test that Atoms convert to reasonable strings."
     */
    @Test
    public void testToString() {
        assertEquals("a", a.toString());
        assertEquals("b", b.toString());
    }
    
    /**
     * Test this has the correct associated LTLType.
     */
    @Test
    public void testType() {
        assertEquals(LTLType.A, a.getLTLType());
        assertEquals(LTLType.A, b.getLTLType());
    }
}
