package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test the LTLFormula representing a temporal logic operator between two elements.
 */
@RunWith(value = Parameterized.class)
public class BinaryTemporalLogicOperatorTest {
    
    private Class<? extends LTLFormula> operator;
    private Class<? extends LTLFormula> oppositeOperator;
    
    private Atom a;
    private Atom b;
    private Atom c;
    
    private LTLFormula a_op_b;
    private LTLFormula a_op_c;
    
    /**
     * Initialize the test with the class and its opposite class.
     * @param operator The operator to test.
     * @param oppositeOperator The opposite of the operator to test.
     */
    public BinaryTemporalLogicOperatorTest(Class<? extends LTLFormula> operator, 
            Class<? extends LTLFormula> oppositeOperator) {
        this.operator = operator;
        this.oppositeOperator = oppositeOperator;
    }
    
    /**
     * Create an instance of the operator.
     * @param left The element on the left side of the operator.
     * @param right The element on the right side of the operator.
     */
    private LTLFormula newInstance(LTLFormula left, LTLFormula right) {
        try {
            Constructor<? extends LTLFormula> cons = 
                operator.getConstructor(LTLFormula.class, LTLFormula.class);
            return cons.newInstance(left, right);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create an instance of the opposite of the operator.
     * @param left The element on the left side of the opposite operator.
     * @param right The element on the right side of the opposite operator.
     */
    private LTLFormula newOppositeInstance(LTLFormula left, LTLFormula right) {
        try {
            Constructor<? extends LTLFormula> cons = 
            oppositeOperator.getConstructor(LTLFormula.class, LTLFormula.class);
            return cons.newInstance(left, right);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * Initialize some common objects to use in all the tests.
     */
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
        c = Atom.get("c");
        a_op_b = newInstance(a, b);
        a_op_c = newInstance(a, c);
    }
    
    /**
     * Test that elements constructed the same are equal, and elements constructed
     * with different parameters are not equal.
     */
    @Test
    public void testEquality() {
        LTLFormula a_op_b_again = newInstance(a, b);
        assertEquals(a_op_b, a_op_b_again);
        assertEquals(a_op_b.hashCode(), a_op_b_again.hashCode());
        assertEquals(0, a_op_b.compareTo(a_op_b_again));
        
        assertFalse(a_op_b.equals(a_op_c));
        assertFalse(0 == a_op_b.compareTo(a_op_c));
    }
    
    /**
     * Test that it produces equal copy elements.
     */
    @Test
    public void testCopy() {
        assertEquals(a_op_b, a_op_b.copy());
        assertEquals(a_op_c, a_op_c.copy());
    }
    
    /**
     * Test the normalization simplification.
     */
    @Test
    public void testNormalization() {
        assertEquals(newInstance(a, b), a_op_b.normalize(false));
        assertEquals(newOppositeInstance(new Negation(a), new Negation(b)), a_op_b.normalize(true));
    }
    
    /**
     * Run this test on all of the temporal logic operators.
     * @return A list of operator-opposite operator pairs.
     */
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { 
            {Since.class, DualSince.class},
            {DualSince.class, Since.class},
            {Until.class, DualUntil.class},
            {DualUntil.class, Until.class},
        };
        return Arrays.asList(data);
    }
}