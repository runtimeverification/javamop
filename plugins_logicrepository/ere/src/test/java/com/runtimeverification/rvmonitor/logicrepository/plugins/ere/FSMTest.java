package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test generating deterministic finite automata from extended regular expressions.
 */
public class FSMTest {
    public static final String NEWLINE = System.getProperty("line.separator");

    private Symbol a;
    private Symbol b;

    @Before
    public void setUp() {
        a = Symbol.get("a");
        b = Symbol.get("b");
    }

    /**
     * Construct the string representation of a DFA matching the given expression.
     *
     * @param expr    The expression to print the DFA/FSM from.
     * @param symbols The symbols present in the expression.
     * @return A string representing the DFA/FSM.
     */
    private String printFSM(ERE expr, Symbol... symbols) {
        FSM dfa = FSM.get(expr, symbols);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        dfa.print(ps);

        return os.toString();
    }

    /**
     * Test generating the DFA for a Concat element of two Symbols.
     */
    @Test
    public void testConcat() {
        ERE ab = Concat.get(a, b);

        String fsm = "s0 [" + NEWLINE + "   a -> s1" + NEWLINE + "]" + NEWLINE +
                "s1 [" + NEWLINE + "   b -> s2" + NEWLINE + "]" + NEWLINE +
                "s2 [" + NEWLINE + "]" + NEWLINE +
                "alias match = s2 " + NEWLINE;
        assertEquals(fsm, printFSM(ab, a, b));
    }

    /**
     * Test generating the DFA for an Or element of two Symbols.
     */
    @Test
    public void testOr() {
        ERE or = Or.get(new ArrayList<ERE>(Arrays.asList(a, b)));

        String fsm = "s0 [" + NEWLINE + "   a -> s1" + NEWLINE + "   b -> s1" + NEWLINE + "]" + NEWLINE +
                "s1 [" + NEWLINE + "]" + NEWLINE +
                "alias match = s1 " + NEWLINE;
        assertEquals(fsm, printFSM(or, a, b));
    }

    /**
     * Test generating the DFA for a Kleene element of one symbol.
     */
    @Test
    public void testKleene() {
        ERE aStar = Kleene.get(a);

        String fsm = "s0 [" + NEWLINE + "   a -> s0" + NEWLINE + "]" + NEWLINE +
                "alias match = s0 " + NEWLINE;
        assertEquals(fsm, printFSM(aStar, a));
    }
}