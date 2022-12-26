package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.LinkedHashMap;
import java.io.PrintStream;
import java.util.LinkedHashSet;

/**
 * A class representing a deterministic finite automata matching an extended regular expression.
 * The class is called "finite state machine", but represents "deterministic finite automata" more closely.
 */
public class FSM {
    public LinkedHashMap<ERE, LinkedHashMap<Symbol, ERE>> contents;
    public LinkedHashSet<ERE> match; 
    private int count = 0;
    private LinkedHashMap<ERE, String> number;
    private ERE start;
    private Symbol[] events;
    
    /**
     * Acquire a deterministic finite automata instance.
     * @param input The input to construct the deterministic finite automata instance with.
     * @param events All the symbols that can be found in the regular expression.
     */
    static public FSM get(ERE input, Symbol[] events) {
        return new FSM(input, events);
    }
    
    /**
     * Construct a deterministic finite automata instance.
     * @param input The input to construct the deterministic finite automata instance with.
     * @param events ALl the symbols that can be found in the regular expression.
     */
    private FSM(ERE input, Symbol[] events) {
        start = input;
        contents = new LinkedHashMap<ERE, LinkedHashMap<Symbol, ERE>>();
        match = new LinkedHashSet<ERE>();
        this.events = events;
        number = new LinkedHashMap<ERE, String>();
        generate(start);
    }
    
    /**
     * Construct all the states that exist as a result of the given regular expression.
     * @param state The regular expression to produce states from.
     */
    private void generate(ERE state) {
        number.put(state, "s" + count++);
        LinkedHashMap<Symbol, ERE> trans = new LinkedHashMap<Symbol, ERE>();
        if(state.containsEpsilon()) {
            match.add(state);
        }
        contents.put(state, trans);
        for(Symbol event : events) {
            ERE next = state.derive(event);
            if(next == Empty.get()) {
                continue;
            }
            trans.put(event, next); 
            if(contents.containsKey(next)) {
                continue;
            }
            generate(next); 
        }
    }
    
    /**
     * Output the deterministic finite automata over a print stream.
     * @param p The stream to output the DFA over.
     */
    public void print(PrintStream p) {
        p.println("s0 [");
        printTransition(contents.get(start), p);
        p.println("]");
        for(ERE state : contents.keySet()) {
            if(state == start) {
                continue;
            }
            p.println(number.get(state) + " [");
            printTransition(contents.get(state), p);
            p.println("]");
        }
        if(match.size() == 0) {
            return;
        }
        p.print("alias match = ");
        for(ERE state : match) {
            p.print(number.get(state) + " ");
        }
        p.println("");
    }
    
    /**
     * Output all the transitions of a given state to other states.
     * @param trans All the transitions coming out of a given state.
     * @param p The stream to output the transitions over.
     */
    private void printTransition(LinkedHashMap<Symbol, ERE> trans, PrintStream p) {
        for(Symbol s : trans.keySet()) {
            p.println("   " + s + " -> " + number.get(trans.get(s)));
        }
    }
}
