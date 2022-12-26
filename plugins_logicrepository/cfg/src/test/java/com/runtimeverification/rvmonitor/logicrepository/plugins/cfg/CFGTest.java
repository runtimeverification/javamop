package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.CFG;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Cursor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.EOF;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Epsilon;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.NonTerminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Production;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Terminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests on the context-free grammar representation and simplification class.
 */
public class CFGTest {
    
    private Terminal a;
    private Terminal b;
    private Terminal c;
    
    private NonTerminal S;
    private NonTerminal T;
    
    private Production Stoab;
    private Production Ttoab;
    private Production Stoabc;
    private Production Stoac;
    private Production Stoca;
    private Production StoSa;
    private Production StoST;
    private Production TtoE;
    
    /**
     * Initialize some common productions and symbols used in constructing the context-free grammars.
     */
    @Before
    public void setUp() {
        a = new Terminal("a");
        b = new Terminal("b");
        c = new Terminal("c");
        
        S = new NonTerminal("S");
        T = new NonTerminal("T");
        
        Stoab = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b)));
        Ttoab = new Production(T, new ArrayList<Symbol>(Arrays.asList(a, b)));
        Stoabc = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, b, c)));
        Stoac = new Production(S, new ArrayList<Symbol>(Arrays.asList(a, c)));
        Stoca = new Production(S, new ArrayList<Symbol>(Arrays.asList(c,a)));
        StoSa = new Production(S, new ArrayList<Symbol>(Arrays.asList(S, a)));
        StoST = new Production(S, new ArrayList<Symbol>(Arrays.asList(S, T)));
        TtoE = new Production(T, new ArrayList<Symbol>(Arrays.asList(new Epsilon())));
    }
    
    /**
     * Test that simplification removes unreachable paths.
     */
    @Test
    public void removeNonReachable() {
        CFG cfg = new CFG();
        cfg.setStart(S);
        cfg.add(Stoab);
        cfg.add(Ttoab);
        
        assertTrue(cfg.getProds().contains(Stoab));
        assertTrue(cfg.getProds().contains(Ttoab));
        cfg.simplify();
        assertTrue(cfg.getProds().contains(Stoab));
        assertFalse(cfg.getProds().contains(Ttoab));
    }
    
    /**
     * Test that epsilon productions are removed except for at the start symbol.
     */
    @Test
    public void removeEpsilons() {
        CFG cfg = new CFG();
        cfg.setStart(S);
        cfg.add(Stoab);
        cfg.add(StoST);
        cfg.add(TtoE);
        
        assertTrue(cfg.getProds().contains(TtoE));
        cfg.simplify();
        assertFalse(cfg.getProds().contains(TtoE));
    }
    
    /**
     * Test that self loops (S -> S) are removed.
     */
    @Test
    public void removeSelfLoops() {
        CFG cfg = new CFG();
        cfg.setStart(S);
        
        Production StoS = new Production(S, new ArrayList<Symbol>(Arrays.asList(S)));
        cfg.add(StoS);
        
        assertTrue(cfg.getProds().contains(StoS));
        cfg.simplify();
        assertFalse(cfg.getProds().contains(StoS));
    }
    
    /**
     * Test that monitors are initialized for the right events and not extraneous events.
     */
    @Test
    public void testCreationEvents() {
        CFG cfg = new CFG();
        cfg.setStart(S);
        cfg.add(Stoab);
        cfg.add(Stoabc);
        cfg.add(Stoac);
        
        cfg.simplify();
        assertEquals("a", cfg.creationEvents());
        
        cfg.add(Stoca);
        cfg.simplify();
        assertTrue("ac".equals(cfg.creationEvents()) || "ca".equals(cfg.creationEvents()));
    }
    
}