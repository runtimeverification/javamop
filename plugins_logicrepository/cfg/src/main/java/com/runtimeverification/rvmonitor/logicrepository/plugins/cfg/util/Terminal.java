package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A fixed string matched in a context-free grammar.
 */
public class Terminal extends Symbol {
    
    /**
     * Construct a named Terminal.
     * @param s The name of the terminal.
     */
    public Terminal(String s) {
        super(s);
    }
    
    /**
     * Construct a Terminal, copying the name of an existing symbol.
     * @param s The symbol to copy the name from.
     */
    public Terminal(Symbol s) {
        super(s.name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
