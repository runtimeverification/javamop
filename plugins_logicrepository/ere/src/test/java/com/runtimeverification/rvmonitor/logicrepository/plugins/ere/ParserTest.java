package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.Arrays;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.parser.EREParser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test parsing ERE elements.
 */
public class ParserTest {
    
    /**
     * Convenience method to produce an ERE from a string.
     * @param str The string to parse the ERE from.
     * @return The parsed ERE.
     */
    private ERE parse(String str) {
        EREParser parser = EREParser.parse(str);
        return parser.getERE();
    }
    
    /**
     * Test parsing an Empty element.
     */
    @Test
    public void testParseEmpty() {
        Empty empty = Empty.get();
        ERE parsed = parse("empty");
        
        assertEquals(empty, parsed);
    }
    
    /**
     * Test parsing an Epsilon element.
     */
    @Test
    public void testParseEpsilon() {
        Epsilon epsilon = Epsilon.get();
        ERE parsed = parse("epsilon");
        
        assertEquals(epsilon, parsed);
    }
    
    /**
     * Test parsing a Symbol element.
     */
    @Test
    public void testParseSymbol() {
        Symbol sym = Symbol.get("a");
        ERE parsed = parse("a");
        
        assertEquals(sym, parsed);
    }
    
    /**
     * Test parsing a Concat element of two symbols.
     */
    @Test
    public void testParseConcat() {
        ERE concat = Concat.get(Symbol.get("a"), Symbol.get("b"));
        ERE parsed = parse("a b");
        
        assertEquals(concat, parsed);
    }
    
    /**
     * Test parsing a Kleene element of a symbol.
     */
    @Test
    public void testParseKleene() {
        ERE kleene = Kleene.get(Symbol.get("a"));
        ERE parsed = parse("a*");
        
        assertEquals(kleene, parsed);
    }
    
    /**
     * Test parsing an Or element of three symbols.
     */
    @Test
    public void testParseOr() {
        ERE or = Or.get(new ArrayList<ERE>(Arrays.asList(Symbol.get("a"), Symbol.get("b"), Symbol.get("c"))));
        ERE parsed = parse("a | b | c");
        
        assertEquals(or, parsed);
    }
    
    /**
     * Test parsing a Negation element of a symbol.
     */
    @Test
    public void testParseNegation() {
        ERE negate = Negation.get(Symbol.get("a"));
        ERE parsed = parse("~a");
        
        assertEquals(negate, parsed);
    }
}