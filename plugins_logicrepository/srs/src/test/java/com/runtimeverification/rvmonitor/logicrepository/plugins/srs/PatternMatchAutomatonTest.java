package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Run tests on the PatternMatchAutomaton class, that actually does the heavy lifting.
 */
public class PatternMatchAutomatonTest {
    
    private static Symbol a = Symbol.get("a");
    private static Symbol b = Symbol.get("b");
    private static Symbol c = Symbol.get("c");
    private static Symbol d = Symbol.get("d");
    
    private static Sequence seqA = new Sequence(new ArrayList<Symbol>(Arrays.asList(a)));
    private static Sequence seqAB = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b)));
    private static Sequence seqABC = new Sequence(new ArrayList<Symbol>(Arrays.asList(a, b, c)));
    private static Sequence seqCD = new Sequence(new ArrayList<Symbol>(Arrays.asList(c, d)));
    
    private static SRS srs;
    private static PatternMatchAutomaton pma;
    
    /**
     * Initialize some objects to support the other tests. Initialize this only once so the states
     * don't get recreated with different indexes for the different tests.
     */
    @BeforeClass
    public static void setUp() {
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
        
        pma = new PatternMatchAutomaton(srs);
    }
    
    /**
     * Test that it can produce the complete transformation system as a string.
     */
    @Test
    public void testString() {
        String pmaStr = pma.toString();
        
        /**
         * These get reordered and the order isn't important, so i just test for the lines separately.
         */
        
        String zeroAtZero = pmaStr.substring(pmaStr.indexOf("\n<0 @ 0"), pmaStr.indexOf("\n<1 @ 1"));
        assertTrue(zeroAtZero.contains("d -> [0] <0 @ 0>"));
        assertTrue(zeroAtZero.contains("c -> [0] <0 @ 0>"));
        assertTrue(zeroAtZero.contains("b -> [0] <0 @ 0>"));
        assertTrue(zeroAtZero.contains("a -> [0] <1 @ 1>"));
        
        String oneAtOne = pmaStr.substring(pmaStr.indexOf("\n<1 @ 1"), pmaStr.indexOf("\n<2 @ 2"));
        assertTrue(oneAtOne.contains("d -> [1] <0 @ 0>"));
        assertTrue(oneAtOne.contains("c -> [1] <0 @ 0>"));
        assertTrue(oneAtOne.contains("a -> [1] <0 @ 0>"));
        assertTrue(oneAtOne.contains("b -> [0] <2 @ 2 matches a b  -> c d >"));
        
        String twoAtTwo = pmaStr.substring(pmaStr.indexOf("\n<2 @ 2"));
        assertTrue(twoAtTwo.contains("d -> [2] <0 @ 0>"));
        assertTrue(twoAtTwo.contains("c -> [2] <0 @ 0>"));
        assertTrue(twoAtTwo.contains("b -> [2] <0 @ 0>"));
        assertTrue(twoAtTwo.contains("a -> [2] <0 @ 0>"));
    }
    
    /**
     * Test that it can produce the complete transformation system as a dot file.
     */
    @Test
    public void testDotSring() {
        String pmaDotStr = pma.toDotString();
        
        System.out.println(pmaDotStr);
        assertTrue(pmaDotStr.contains("s_0 [texlbl=\"$\\begin{array}{c}0 : 0\\end{array}$\" label=\"       \"];"));
        assertTrue(pmaDotStr.contains("s_1 [texlbl=\"$\\begin{array}{c}1 : 1\\end{array}$\" label=\"       \"];"));
        assertTrue(pmaDotStr.contains("s_2 [texlbl=\"$\\begin{array}{c}2 : 2\\\\ (a\\ b \\rightarrow c\\ d)\\end{array}$\" label=\"           \"];"));
        assertTrue(pmaDotStr.contains("s_0 -> s_1 [label=\"a / 0\"];"));
        assertTrue(pmaDotStr.contains("s_1 -> s_2 [label=\"b / 0\"];"));
    }
    
}