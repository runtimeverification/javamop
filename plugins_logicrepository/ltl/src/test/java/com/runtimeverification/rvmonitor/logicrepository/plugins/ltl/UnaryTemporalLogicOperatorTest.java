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
public class UnaryTemporalLogicOperatorTest {
    
    private Class<? extends LTLFormula> operator;
    private Class<? extends LTLFormula> oppositeOperator;
    
    private Atom a;
    private Atom b;
    private Atom c;
    
    private LTLFormula op_a;
    private LTLFormula op_b;
    
    /**
     * Initialize the test with the class and its opposite class.
     * @param operator The operator to test.
     * @param oppositeOperator The opposite of the operator to test.
     */
    public UnaryTemporalLogicOperatorTest(Class<? extends LTLFormula> operator, 
            Class<? extends LTLFormula> oppositeOperator) {
        this.operator = operator;
        this.oppositeOperator = oppositeOperator;
    }
    
    /**
     * Create an instance of the operator.
     * @param left The element on the left side of the operator.
     * @param right The element on the right side of the operator.
     */
    private LTLFormula newInstance(LTLFormula elem) {
        try {
            Constructor<? extends LTLFormula> cons = 
                operator.getConstructor(LTLFormula.class);
            return cons.newInstance(elem);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create an instance of the opposite of the operator.
     * @param left The element on the left side of the opposite operator.
     * @param right The element on the right side of the opposite operator.
     */
    private LTLFormula newOppositeInstance(LTLFormula elem) {
        try {
            Constructor<? extends LTLFormula> cons = 
            oppositeOperator.getConstructor(LTLFormula.class);
            return cons.newInstance(elem);
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
        op_a = newInstance(a);
        op_b = newInstance(b);
    }
    
    /**
     * Test that elements constructed the same are equal, and elements constructed
     * with different parameters are not equal.
     */
    @Test
    public void testEquality() {
        LTLFormula op_a_again = newInstance(a);
        assertEquals(op_a, op_a_again);
        assertEquals(op_a.hashCode(), op_a_again.hashCode());
        assertEquals(0, op_a.compareTo(op_a));
        
        assertFalse(op_a.equals(op_b));
        assertFalse(0 == op_a.compareTo(op_b));
    }
    
    /**
     * Test that it produces equal copy elements.
     */
    @Test
    public void testCopy() {
        assertEquals(op_a, op_a.copy());
        assertEquals(op_b, op_b.copy());
    }
    
    /**
     * Test the normalization simplification.
     */
    @Test
    public void testNormalization() {
        assertEquals(newInstance(a), op_a.normalize(false));
        assertEquals(newOppositeInstance(new Negation(a)), op_a.normalize(true));
    }
    
    /**
     * Run this test on all of the temporal logic operators.
     * @return A list of operator-opposite operator pairs.
     */
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
            {Previously.class, DualPreviously.class},
            {DualPreviously.class, Previously.class},
            {Next.class, DualNext.class},
            {DualNext.class, Next.class},
        };
        return Arrays.asList(data);
    }
}