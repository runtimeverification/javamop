package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A symbol that has at least one expansion into other symbols in a context-free grammar.
 */
public class NonTerminal extends Symbol{
    
    /**
     * Construct a nonterminal with the given name.
     * @param s The name of the nonterminal.
     */
    public NonTerminal(String s) {
        super(s);
    }
    
    /**
     * Construct a NonTerminal, taking the name from another Symbol.
     * @param s The symbol to take the name from.
     */
    public NonTerminal(Symbol s) {
        super(s.name);
    }
    
    @Override
    public String toString() {
        return "nt("+name+")";
    }
}
