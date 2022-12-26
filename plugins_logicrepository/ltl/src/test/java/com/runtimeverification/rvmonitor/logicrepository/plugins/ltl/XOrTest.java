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
 * Test the LTLFormula representing a logical XOr of a number of elements.
 */
public class XOrTest {
    
    /**
     * Convenience method to produce XOr elements out of a varargs parameter.
     * @param elements The members of the XOr.
     * @return An XOr element over the given elements.
     */
    private static XOr makeXOr(LTLFormula... elements) {
        ArrayList<LTLFormula> arrList = new ArrayList<LTLFormula>();
        arrList.addAll(Arrays.asList(elements));
        Collections.sort(arrList);
        return new XOr(arrList);
    }
    
    private Atom a;
    private Atom b;
    private Atom c;
    
    private XOr a_xor_b;
    private XOr a_xor_c;
    
    /**
     * Initialize some common objects to use in all the tests.
     */
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
        c = Atom.get("c");
        a_xor_b = makeXOr(a, b);
        a_xor_c = makeXOr(a, c);
    }
    
    /**
     * Test that XOr elements constructed with the same parameters are equal, and ones constructed
     * with different parameters are not equal.
     */
    @Test
    public void testEquality() {
        XOr a_xor_b_again = makeXOr(a, b);
        assertEquals(a_xor_b, a_xor_b_again);
        assertEquals(a_xor_b.hashCode(), a_xor_b_again.hashCode());
        assertEquals(0, a_xor_b.compareTo(a_xor_b_again));
        
        assertFalse(a_xor_b.equals(a_xor_c));
        assertFalse(0 == a_xor_b.compareTo(a_xor_c));
    }
    
    /**
     * Ensure that Or elements have the correct ERE type.
     */
    @Test
    public void testType() {
        assertEquals(LTLType.XOR, a_xor_b.getLTLType());
        assertEquals(LTLType.XOR, a_xor_c.getLTLType());
    }
    
    /**
     * Test the lowering simplification.
     */
    @Test
    public void testLower() {
        LTLFormula expected = OrTest.makeOr(AndTest.makeAnd(new Negation(a), b),
            AndTest.makeAnd(a, new Negation(b)));
        assertEquals(expected, a_xor_b.simplify());
    }
    
    /**
     * XOr elements should be able to produce equal copies.
     */
    @Test
    public void testCopy() {
        assertEquals(a_xor_b, a_xor_b.copy());
        assertEquals(a_xor_c, a_xor_c.copy());
    }
}
