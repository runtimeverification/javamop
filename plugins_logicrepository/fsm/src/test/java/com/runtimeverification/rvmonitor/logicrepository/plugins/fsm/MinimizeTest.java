package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Symbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Transition;


import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class MinimizeTest {
    
    @Test
    public void minimizeTwoToOne() {
        
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
        
        FSMMin min = new FSMMin(start, symbols, states, categories, aliases, transitions);
        
        assertEquals(1, min.getStates().size());
    }
    
    @Test
    public void minimizeCategories() {
        
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
        
        FSMMin min = new FSMMin(start, symbols, states, categories, aliases, transitions);
        
        assertEquals(2, min.getStates().size());
    }
}