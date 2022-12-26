package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the LTLFormula representing the logical and of multiple elements.
 */
public class AndTest {
    
    /**
     * Convenience method to produce And elements out of a varargs parameter.
     * @param elements The members of the And.
     * @return An And element over the given elements.
     */
    public static And makeAnd(LTLFormula... elements) {
        ArrayList<LTLFormula> arrList = new ArrayList<LTLFormula>();
        arrList.addAll(Arrays.asList(elements));
        Collections.sort(arrList);
        return new And(arrList);
    }
    
    private Atom a;
    private Atom b;
    private Atom c;
    private And a_and_b;
    private And a_and_c;
    
    /**
     * Initialize some commonly used members.
     */
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
        c = Atom.get("c");
        a_and_b = makeAnd(a, b);
        a_and_c = makeAnd(a, c);
    }
    
    /**
     * Ensure that And elements constructed the same way are equal, and ones constructed other ways
     * are not equal.
     */
    @Test
    public void testEquality() {
        And a_and_b_again = makeAnd(a, b);
        assertEquals(a_and_b, a_and_b_again);
        assertEquals(a_and_b.hashCode(), a_and_b_again.hashCode());
        assertEquals(0, a_and_b.compareTo(a_and_b_again));
        
        assertFalse(a_and_b.equals(a_and_c));
        assertFalse(0 == a_and_b.compareTo(a_and_c));
    }
    
    /**
     * Ensure that And elements have the correct ERE type.
     */
    @Test
    public void testType() {
        assertEquals(LTLType.AND, a_and_b.getLTLType());
        assertEquals(LTLType.AND, a_and_c.getLTLType());
    }
    
    /**
     * Test the normalization step of simplification.
     */
    @Test
    public void testNormalize() {
        {
            ArrayList<LTLFormula> orElements = new ArrayList<LTLFormula>(
                Arrays.asList(new Negation(a), new Negation(b)));
            Collections.sort(orElements);
            Or expected = new Or(orElements);
            assertEquals(expected, a_and_b.copy().normalize(true));
        }
        assertEquals(makeAnd(a, c), a_and_c.normalize(false));
    }
    
    /**
     * Test the reduction step of simplification.
     */
    @Test
    public void testReduce() {
        {
            And duplicate = makeAnd(a, b, c, a, b, c);
            assertEquals(makeAnd(a, b, c), duplicate.reduce());
        }
        {
            And combined = makeAnd(a_and_b,a_and_c);
            assertEquals(makeAnd(a, b, c), combined.reduce());
        }
        {
            And withTrue = makeAnd(a, b, True.get());
            assertEquals(a_and_b, withTrue.reduce());
        }
        {
            And withFalse = makeAnd(a, b, False.get());
            assertEquals(False.get(), withFalse.reduce());
        }
        {
            And solitaryWithTrue = makeAnd(a, True.get());
            assertEquals(a, solitaryWithTrue.reduce());
        }
    }
    
    /**
     * Test that And elements can produce equal copies.
     */
    @Test
    public void testCopy() {
        assertEquals(a_and_b, a_and_b.copy());
        assertEquals(a_and_c, a_and_c.copy());
    }
}
