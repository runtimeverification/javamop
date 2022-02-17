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
 * Test the LTLFormula representing a logical Or of a number of elements.
 */
public class OrTest {
    
    /**
     * Convenience method to produce Or elements out of a varargs parameter.
     * @param elements The members of the Or.
     * @return An Or element over the given elements.
     */
    public static Or makeOr(LTLFormula... elements) {
        ArrayList<LTLFormula> arrList = new ArrayList<LTLFormula>();
        arrList.addAll(Arrays.asList(elements));
        Collections.sort(arrList);
        return new Or(arrList);
    }
    
    private Atom a;
    private Atom b;
    private Atom c;
    
    private Or a_or_b;
    private Or a_or_c;
    
    /**
     * Initialize some common objects to use in all the tests.
     */
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
        c = Atom.get("c");
        a_or_b = makeOr(a, b);
        a_or_c = makeOr(a, c);
    }
    
    /**
     * Test that Or elements constructed with the same parameters are equal, and ones constructed
     * with different parameters are not equal.
     */
    @Test
    public void testEquality() {
        Or a_or_b_again = makeOr(a, b);
        assertEquals(a_or_b, a_or_b_again);
        assertEquals(a_or_b.hashCode(), a_or_b_again.hashCode());
        assertEquals(0, a_or_b.compareTo(a_or_b_again));
        
        assertFalse(a_or_b.equals(a_or_c));
        assertFalse(0 == a_or_b.compareTo(a_or_c));
    }
    
    /**
     * Ensure that Or elements have the correct ERE type.
     */
    @Test
    public void testType() {
        assertEquals(LTLType.OR, a_or_b.getLTLType());
        assertEquals(LTLType.OR, a_or_c.getLTLType());
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
            And expected = new And(orElements);
            assertEquals(expected, a_or_b.copy().normalize(true));
        }
        assertEquals(makeOr(a, c), a_or_c.normalize(false));
    }
    
    /**
     * Test the reduction step of simplification.
     */
    @Test
    public void testReduce() {
        {
            Or duplicate = makeOr(a, b, c, a, b, c);
            assertEquals(makeOr(a, b, c), duplicate.reduce());
        }
        {
            Or combined = makeOr(a_or_b,a_or_c);
            assertEquals(makeOr(a, b, c), combined.reduce());
        }
        {
            Or withTrue = makeOr(a, b, True.get());
            assertEquals(True.get(), withTrue.reduce());
        }
        {
            Or withFalse = makeOr(a, b, False.get());
            assertEquals(a_or_b, withFalse.reduce());
        }
    }
    
    /**
     * Test that Or elements can produce equal copies.
     */
    @Test
    public void testCopy() {
        assertEquals(a_or_b, a_or_b.copy());
        assertEquals(a_or_c, a_or_c.copy());
    }
}
