package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AAutomatonTest {
    
    private Atom a;
    private Atom b;
    private Atom c;
    
    @Before
    public void setUp() {
        a = Atom.get("a");
        b = Atom.get("b");
        c = Atom.get("c");
    }
    
    @Test
    public void testAnd() {
        LTLFormula formula = AndTest.makeAnd(a, b, c).simplify();
        AAutomaton aa = new AAutomaton(formula);
        System.out.println(aa);
        
        assertEquals(5, aa.Q.size());
        assertTrue(aa.Q.contains(a));
        assertTrue(aa.Q.contains(b));
        assertTrue(aa.Q.contains(c));
        assertTrue(aa.Q.contains(formula));
        assertTrue(aa.Q.contains(END.get()));
        
        assertEquals(1, aa.I.size());
        
        assertTrue(aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, b, c))) ||
            aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, c, b))) ||
            aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(b, a, c))) ||
            aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(b, c, a))) ||
            aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(c, a, b))) ||
            aa.I.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(c, b, a))));
        
        assertEquals(4, aa.R.size());
        assertTrue(aa.R.contains(a));
        assertTrue(aa.R.contains(b));
        assertTrue(aa.R.contains(c));
        assertTrue(aa.R.contains(formula));
        
        assertTrue(aa.D.containsKey(formula));
        assertEquals(1, aa.D.get(formula).tuples.size());
        ATuple dFormula = aa.D.get(formula).tuples.iterator().next();
        assertEquals(dFormula.previous, new LinkedHashSet<LTLFormula>());
        assertTrue(dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, b, c))) ||
            dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, c, b))) ||
            dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(b, a, c))) ||
            dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(b, c, a))) ||
            dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(c, a, b))) ||
            dFormula.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(c, b, a))));
        assertEquals(dFormula.next, new LinkedHashSet<LTLFormula>());
        
        assertTrue(aa.D.containsKey(END.get()));
        assertEquals(0, aa.D.get(END.get()).tuples.size());
        
        assertTrue(aa.D.containsKey(a));
        assertEquals(1, aa.D.get(a).tuples.size());
        ATuple dA = aa.D.get(a).tuples.iterator().next();
        assertEquals(dA.previous, new LinkedHashSet<LTLFormula>());
        assertEquals(dA.next, new LinkedHashSet<LTLFormula>());
        assertTrue(dA.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a))));
        assertTrue(dA.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, b))) ||
            dA.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(b, a))));
        assertTrue(dA.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(a, c))) ||
            dA.symbols.contains(new LinkedHashSet<LTLFormula>(Arrays.asList(c, a))));
    }
}