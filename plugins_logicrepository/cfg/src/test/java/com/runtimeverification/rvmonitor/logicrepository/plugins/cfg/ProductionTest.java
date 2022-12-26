package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Epsilon;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.NonTerminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Production;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Terminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Production class, representing a mapping from a NonTerminal to some output symbols.
 */
public class ProductionTest {
    
    private Terminal a;
    private Terminal b;
    private Terminal c;
    
    private NonTerminal S;
    private NonTerminal T;
    
    private Production Stoab;
    private Production Stoabc;
    private Production Stoac;
    private Production StoSa;
    private Production StoST;
    
    /**
     * Initialize some commonly used symbols and productions for the other tests.
     */
    @Before
    public void setUp() {
        a = new Terminal("a");
        b = new Terminal("b");
        c = new Terminal("c");
        
        S = new NonTerminal("S");
        T = new NonTerminal("T");
        
        Stoab = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b)));
        Stoabc = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b, c)));
        Stoac = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, c)));
        StoSa = new Production(S, new ArrayList<Symbol>(Arrays.asList(S, a)));
        StoST = new Production(S, new ArrayList<Symbol>(Arrays.asList(S, T)));
    }
    
    /**
     * Test conversion of Productions to human-readable strings.
     */
    @Test
    public void testToString() {
        assertEquals(Stoabc.toString(), "nt(S) -> a b c");
        assertEquals(StoSa.toString(), "nt(S) -> nt(S) a");
    }
    
    /**
     * Test productions testing membership on Symbols.
     */
    @Test
    public void testContains() {
        assertFalse(Stoabc.contains(S));
        assertTrue(Stoabc.contains(a));
        assertTrue(Stoabc.contains(b));
        assertTrue(Stoabc.contains(c));
        
        assertFalse(Stoab.contains(S));
        assertTrue(Stoab.contains(a));
        assertTrue(Stoab.contains(b));
        assertFalse(Stoab.contains(c));
        
        assertTrue(StoSa.contains(S));
        assertTrue(StoSa.contains(a));
        assertFalse(StoSa.contains(b));
        assertFalse(StoSa.contains(c));
    }
    
    /**
     * Test equality comparison between productions.
     */
    @Test
    public void testEquals() {
        Production another_Stoabc = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b, c)));
        assertEquals(Stoabc, another_Stoabc);
        assertEquals(Stoabc.hashCode(), another_Stoabc.hashCode());
        
        assertFalse(Stoab.equals(Stoabc));
        assertFalse(StoSa.equals(StoST));
    }
    
    /**
     * Test that productions can produce independent copies.
     */
    @Test
    public void testClone() {
        Production another_Stoab = Stoab.clone();
        assertEquals(Stoab, another_Stoab);
        
        Stoab.getRhs().add(c);
        assertEquals(3, Stoab.getRhs().size());
        assertEquals(2, another_Stoab.getRhs().size());
        assertFalse(Stoab.equals(another_Stoab));
    }
    
    /**
     * Test that independent copy productions can be made from existing productions.
     */
    @Test
    public void testCopy() {
        Production another_Stoab = new Production(Stoab);
        assertEquals(Stoab, another_Stoab);
        
        Stoab.getRhs().add(c);
        assertEquals(3, Stoab.getRhs().size());
        assertEquals(2, another_Stoab.getRhs().size());
        assertFalse(Stoab.equals(another_Stoab));
    }
    
    /**
     * Test Productions identifying all the member nonterminals.
     */
    @Test
    public void testNonTerminals() {
        HashSet<NonTerminal> expected;
        
        expected = new HashSet<NonTerminal>(Arrays.asList(S));
        assertEquals(expected, StoSa.nonTerminals());
        
        expected = new HashSet<NonTerminal>(Arrays.asList(S, T));
        assertEquals(expected, StoST.nonTerminals());
        
        expected = new HashSet<NonTerminal>();
        assertEquals(expected, Stoabc.nonTerminals());
    }
    
    /**
     * Test productions replacing member nonterminals with other nonterminals.
     */
    @Test
    public void testReplace() {
        assertTrue(StoSa.contains(S));
        assertFalse(StoSa.contains(T));
        StoSa.replaceRHSNTs(T, S);
        assertFalse(StoSa.contains(S));
        assertTrue(StoSa.contains(T));
        
        assertTrue(StoST.contains(S));
        assertTrue(StoST.contains(T));
        StoST.replaceRHSNTs(T, S);
        assertFalse(StoST.contains(S));
        assertTrue(StoST.contains(T));
    }
    
    /**
     * Test finding fragments of the Production before given symbols.
     */
    @Test
    public void testBeforeSym() {
        ArrayList<Symbol> beforeC = Stoabc.beforeSym(c);
        assertEquals(2, beforeC.size());
        assertEquals(new ArrayList<Symbol>(Arrays.asList(a, b)), beforeC);
        
        Production Stoabab = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b, a, b)));
        HashSet<ArrayList<Symbol>> beforeBset = Stoabab.beforeSymS(b);
        assertEquals(2, beforeBset.size());
        assertTrue(beforeBset.contains(new ArrayList<Symbol>(Arrays.asList(a))));
        assertTrue(beforeBset.contains(new ArrayList<Symbol>(Arrays.asList(a, b, a))));
    }
}
