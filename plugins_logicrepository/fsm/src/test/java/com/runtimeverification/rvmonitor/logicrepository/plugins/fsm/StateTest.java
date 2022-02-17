package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.State;

import org.junit.Test;
import static org.junit.Assert.*;

public class StateTest {
    
    @Test
    public void testEquality() {
        State first = State.get("first");
        State first_again = State.get("first");
        State second = State.get("second");
        
        assertEquals(first, first_again);
        assertFalse(first.equals(second));
        assertFalse(first_again.equals(second));
        
        assertEquals(first.hashCode(), first_again.hashCode());
        assertFalse(first.hashCode() == second.hashCode());
    }
    
    @Test
    public void testToString() {
        assertEquals("first", State.get("first").toString());
        assertEquals("abc", State.get("abc").toString());
    }
}