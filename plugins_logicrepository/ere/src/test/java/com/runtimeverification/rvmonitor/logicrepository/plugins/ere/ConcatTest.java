package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Concat ERE operator.
 * @author A. Cody Schuffelen
 */
public class ConcatTest {
    
    private Symbol a;
    private Symbol b;
    private Symbol c;
    
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        c = Symbol.get("c");
    }
    
    /**
     * Test that equivalent concat operators compare equal.
     */
    @Test
    public void testEquality() {
        ERE one = Concat.get(a, b);
        ERE two = Concat.get(a, b);
        assertEquals(one, two);
        assertEquals(0, one.compareTo(two));
    }
    
    /**
     * Test that different concat operators compare inequal.
     */
    @Test
    public void testInequality() {
        ERE one = Concat.get(a, b);
        ERE two = Concat.get(a, c);
        
        assertFalse(one.equals(two));
        assertFalse(0 == one.compareTo(two));
        assertFalse(one.equals(a));
        assertFalse(0 == one.compareTo(a));
    }
    
    /**
     * Test that the it has the correct EREType.
     */
    @Test
    public void testEREType() {
        ERE one = Concat.get(a, b);
        assertEquals(EREType.CAT, one.getEREType());
    }
    
    /**
     * Test that concat operators convert to strings properly
     */
    @Test
    public void testString() {
        ERE one = Concat.get(a, b);
        assertEquals("(a b)", one.toString());
    }
    
    /**
     * Test that concat contains an epsilon only if both members are epsilons.
     */
    @Test
    public void testContainsEpsilon() {
        Epsilon epsilon = Epsilon.get();
        
        assertFalse(Concat.get(a, b).containsEpsilon());
        assertFalse(Concat.get(a, epsilon).containsEpsilon());
        assertFalse(Concat.get(epsilon, b).containsEpsilon());
        assertTrue(Concat.get(epsilon, epsilon).containsEpsilon());
    }
    
    /**
     * Test that concat elements with empty or epsilon members reduce to simpler EREs.
     */
    @Test
    public void testSimplify() {
        Epsilon epsilon = Epsilon.get();
        Empty empty = Empty.get();
        
        assertEquals(empty, Concat.get(a, empty));
        assertEquals(empty, Concat.get(empty, a));
        assertEquals(a, Concat.get(a, epsilon));
        assertEquals(a, Concat.get(epsilon, a));
        
        assertEquals(empty, Concat.get(epsilon, empty));
        assertEquals(empty, Concat.get(empty, epsilon));
    }
    
    /**
     * Test that concat copy produces equivalent members.
     */
    @Test
    public void testCopy() {
        ERE concat = Concat.get(a, b);
        ERE copy = concat.copy();
        assertEquals(concat, copy);
    }
    
    /**
     * Test that concat EREs derive properly.
     * There is a special case where when the first member is able to match epsilon, derive must also
     * attempt to match with the second element.
     */
    @Test
    public void testDerive() {
        Epsilon epsilon = Epsilon.get();
        Empty empty = Empty.get();
        
        ERE ab = Concat.get(a, b);
        assertEquals(b, ab.derive(a));
        assertEquals(empty, ab.derive(b));
        
        ERE aStar = Kleene.get(a);
        ERE aStarb = Concat.get(aStar, b);
        assertEquals(aStarb, aStarb.derive(a));
        assertEquals(epsilon, aStarb.derive(b));
        assertEquals(empty, aStarb.derive(c));
    }
}