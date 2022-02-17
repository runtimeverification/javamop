package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Symbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Transition;


import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnablesTest {
    
    @Test
    public void testSimple() {
        State start = State.get("start");
        State extra = State.get("extra");
        ArrayList<State> states = new ArrayList<State>();
        states.add(start);
        states.add(extra);
        
        Symbol a = Symbol.get("a");
        Symbol b = Symbol.get("b");
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        symbols.add(a);
        symbols.add(b);
        
        ArrayList<State> categories = new ArrayList<State>();
        categories.add(start);
        
        HashMap<State, HashSet<State>> aliases = new HashMap<State, HashSet<State>>();
        
        Transition fromStart = new Transition();
        fromStart.put(a, extra);
        fromStart.put(b, extra);
        
        Transition fromExtra = new Transition();
        fromExtra.put(a, start);
        fromExtra.put(b, start);
        
        HashMap<State, Transition> transitions = new HashMap<State, Transition>();
        transitions.put(start, fromStart);
        transitions.put(extra, fromExtra);
        
        FSMEnables enableObject = new FSMEnables(start, symbols, states, categories, aliases, transitions);
        
        HashMap<State, HashMap<Symbol, HashSet<HashSet<Symbol>>>> enables = enableObject.getEnables();
        
        assertTrue(enables.containsKey(start));
        assertFalse(enables.containsKey(extra));
        
        HashMap<Symbol, HashSet<HashSet<Symbol>>> startEnables = enables.get(start);
        
        HashSet<HashSet<Symbol>> symbolsA = startEnables.get(a);
        HashSet<HashSet<Symbol>> symbolsB = startEnables.get(b);
        
        HashSet<Symbol> empty = new HashSet<Symbol>();
        HashSet<Symbol> setA = new HashSet<Symbol>(Arrays.asList(a));
        HashSet<Symbol> setB = new HashSet<Symbol>(Arrays.asList(b));
        HashSet<Symbol> setAB = new HashSet<Symbol>(Arrays.asList(a, b));
        
        assertTrue(symbolsA.contains(empty));
        assertTrue(symbolsA.contains(setA));
        assertTrue(symbolsA.contains(setB));
        assertTrue(symbolsA.contains(setAB));
        
        assertTrue(symbolsB.contains(empty));
        assertTrue(symbolsB.contains(setA));
        assertTrue(symbolsB.contains(setB));
        assertTrue(symbolsB.contains(setAB));
    }
    
}