package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for individual rules in the String Rewrite System.
 */
public class RuleTest {
    
    private Symbol a, b, c, d;
    private Rule ab_a;
    private Rule ab_cd;
    
    /**
     * Initialize some objects to use in the other tests.
     */
    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
        c = Symbol.get("c");
        d = Symbol.get("d");
        
        Sequence seqA = new Sequence(new ArrayList<Symbol>(Arrays.asList(a)));
        Sequence seqAB = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b)));
        Sequence seqCD = new Sequence(new ArrayList<Symbol>(Arrays.asList(c, d)));
        
        ab_a = new Rule(seqAB, seqA);
        ab_cd = new Rule(seqAB, seqCD);
    }
    
    /**
     * Test that initialization is performed properly.
     */
    @Test
    public void testInit() {
        assertFalse(ab_a.getNumber() == ab_cd.getNumber());
    }
    
    /**
     * Test identifying the terminals in the rules.
     */
    @Test
    public void testTerminals() {
        assertTrue(ab_a.getTerminals().contains(a));
        assertTrue(ab_a.getTerminals().contains(b));
        assertFalse(ab_a.getTerminals().contains(c));
        assertFalse(ab_a.getTerminals().contains(d));
        
        assertTrue(ab_cd.getTerminals().contains(a));
        assertTrue(ab_cd.getTerminals().contains(b));
        assertTrue(ab_cd.getTerminals().contains(c));
        assertTrue(ab_cd.getTerminals().contains(d));
    }
    
    /**
     * Test conversions to strings.
     */
    @Test
    public void testString() {
        assertEquals("a b  -> a ", ab_a.toString());
        assertEquals("a b  -> c d ", ab_cd.toString());
    }
    
    /**
     * Test conversions to dot strings.
     */
    @Test
    public void testDotString() {
        assertEquals("a\\ b \\rightarrow a", ab_a.toDotString());
        assertEquals("a\\ b \\rightarrow c\\ d", ab_cd.toDotString());
    }
}
