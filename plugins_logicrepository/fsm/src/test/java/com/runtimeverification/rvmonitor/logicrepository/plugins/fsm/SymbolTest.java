package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Symbol;

import org.junit.Test;
import static org.junit.Assert.*;

public class SymbolTest {
    
    @Test
    public void testEquality() {
        Symbol first = Symbol.get("first");
        Symbol first_again = Symbol.get("first");
        Symbol second = Symbol.get("second");
        
        assertEquals(first, first_again);
        assertFalse(first.equals(second));
        assertFalse(first_again.equals(second));
        
        assertEquals(first.hashCode(), first_again.hashCode());
        assertFalse(first.hashCode() == second.hashCode());
    }
    
    @Test
    public void testToString() {
        assertEquals("first", Symbol.get("first").toString());
        assertEquals("abc", Symbol.get("abc").toString());
    }
}