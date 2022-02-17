package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the class representing a sequence of symbols.
 */
public class SequenceTest {
    
    private Symbol a, b, c, d;
    
    private Sequence empty;
    private Sequence sequenceAB;
    private Sequence sequenceABC;
    
    /**
     * Initialize some sequences to use in the other tests.
     */
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        c = Symbol.get("c");
        d = Symbol.get("d");
        
        empty = new Sequence();
        sequenceAB = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b)));
        sequenceABC = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b, c)));
    }
    
    /**
     * Test conversion of Sequences to strings.
     */
    @Test
    public void stringTest() {
        assertEquals("#epsilon", empty.toString());
        assertEquals("a b ", sequenceAB.toString());
        assertEquals("a b c ", sequenceABC.toString());
    }
    
    /**
     * Test conersion of Sequences to dot strings.
     */
    @Test
    public void dotStringTest() {
        assertEquals("\\#epsilon", empty.toDotString());
        assertEquals("a\\ b", sequenceAB.toDotString());
        assertEquals("a\\ b\\ c", sequenceABC.toDotString());
    }
    
    /**
     * Test that Sequences produce distinct copies that don't affect the original.
     */
    @Test
    public void testCopy() {
        Sequence[] copies = { new Sequence(sequenceABC), sequenceABC.copy() };
        for(Sequence sequenceABCD : copies) {
            assertEquals(3, sequenceABCD.size());
            assertEquals(sequenceABC, sequenceABCD);
            
            sequenceABCD.add(d);
            assertEquals(4, sequenceABCD.size());
            assertEquals(3, sequenceABC.size());
            assertFalse(sequenceABC.equals(sequenceABCD));
        }
    }
    
    /**
     * Test that sequences can judge the length of their dot strings.
     */
    @Test
    public void testDotLength() {
        assertEquals(8, empty.dotLength());
        assertEquals(2, sequenceAB.dotLength());
        assertEquals(3, sequenceABC.dotLength());
    }
    
    /**
     * Test the implementation generation from Sequences.
     */
    @Test
    public void testImpl() {
        HashMap<Symbol, Integer> numberMapping = new HashMap<Symbol, Integer>();
        numberMapping.put(a, 1);
        numberMapping.put(b, 4);
        numberMapping.put(c, 2);
        numberMapping.put(d, 3);
        
        StringBuilder builder = new StringBuilder();
        sequenceABC.getImpl(builder, numberMapping);
        assertEquals(", new int[] {1,4,2,}", builder.toString());
        
        builder = new StringBuilder();
        sequenceAB.getImpl(builder, numberMapping);
        assertEquals(", new int[] {1,4,}", builder.toString());
    }
}
