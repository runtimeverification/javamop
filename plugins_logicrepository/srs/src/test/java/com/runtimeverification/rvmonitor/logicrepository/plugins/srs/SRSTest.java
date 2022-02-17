package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the complete String Rewrite System class. This class holds all the rules for a string
 * rewrite system,but doesn't actually have that many operations, so there aren't many tests here.
 */
public class SRSTest {
    
    private Symbol a = Symbol.get("a");
    private Symbol b = Symbol.get("b");
    private Symbol c = Symbol.get("c");
    private Symbol d = Symbol.get("d");
    
    private Sequence seqA = new Sequence(new ArrayList<Symbol>(Arrays.asList(a)));
    private Sequence seqAB = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b)));
    private Sequence seqABC = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b, c)));
    private Sequence seqCD = new Sequence(new ArrayList<Symbol>(Arrays.asList(c, d)));
    
    private SRS srs;
    
    /**
     * Initialize some objects to support the other tests.
     */
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        c = Symbol.get("c");
        d = Symbol.get("d");
        
        seqA = new Sequence(new ArrayList<Symbol>(Arrays.asList(a)));
        seqAB = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b)));
        seqABC = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b, c)));
        seqCD = new Sequence(new ArrayList<Symbol>(Arrays.asList(c, d)));
        
        Rule ab_a = new Rule(seqAB, seqA);
        Rule ab_cd = new Rule(seqAB, seqCD);
        
        srs = new SRS();
        srs.add(ab_a);
        srs.add(ab_cd);
    }
    
    /**
     * Test conversion to strings.
     */
    @Test
    public void testString() {
        assertEquals("a b  -> a ,\na b  -> c d ,\n", srs.toString());
        assertEquals("&a b  -> a .\n&a b  -> c d .\n", srs.toPaddedString("&"));
    }
    
    /**
     * Test calculated properties.
     */
    @Test
    public void testProperties() {
        assertTrue(srs.getTerminals().contains(a));
        assertTrue(srs.getTerminals().contains(b));
        assertTrue(srs.getTerminals().contains(c));
        assertTrue(srs.getTerminals().contains(d));
        
        assertEquals(2, srs.getLongestLhsSize());
        srs.add(new Rule(seqABC, seqAB));
        assertEquals(3, srs.getLongestLhsSize());
        
        assertTrue(srs.getTerminals().contains(a));
        assertTrue(srs.getTerminals().contains(b));
        assertTrue(srs.getTerminals().contains(c));
        assertTrue(srs.getTerminals().contains(d));
    }
    
}
