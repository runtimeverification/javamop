package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Symbol class used in SRS expressions.
 */
public class SymbolTest {
    
    private Symbol foo;
    private Symbol bar;
    
    /**
     * Initialize some symbols to be used.
     */
    @Before
    public void setUp() {
        foo = Symbol.get("foo");
        bar = Symbol.get("bar");
    }
    
    /**
     * Test that Symbols with the same name are equal and have the same hashCode.
     */
    @Test
    public void testEquality() {
        Symbol foo_again = Symbol.get("foo");
        Symbol bar_again = Symbol.get("bar");
        
        assertEquals(foo, foo_again);
        assertEquals(bar, bar_again);
        assertFalse(foo.equals(bar));
        assertFalse(foo.equals(bar_again));
        
        assertEquals(foo.hashCode(), foo_again.hashCode());
        assertEquals(bar.hashCode(), bar_again.hashCode());
    }
    
    /**
     * Test conversion of Symbols to strings.
     */
    @Test
    public void testString() {
        assertEquals("foo", foo.toString());
        assertEquals("bar", bar.toString());
    }
    
    /**
     * Test conversion of Symbols to dot strings.
     */
    @Test
    public void testDotString() {
        assertEquals("foo", foo.toDotString());
        assertEquals("bar", bar.toDotString());
        assertEquals("\\mathbin{\\char`\\^}", Symbol.get("^").toDotString());
        assertEquals("\\$", Symbol.get("$").toDotString());
    }
}