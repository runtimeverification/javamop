package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * Anything that can be recognized as part of a context-free grammar.
 */
public class Symbol implements java.io.Serializable {
    public final String name;
    
    /**
     * Construct a named symbol.
     * @param s The name of the symbol.
     */
    public Symbol(String s) {
        name = s;
    }
    
    @Override
    public String toString() {
        return "sym("+name+")";
    }
    
    @Override
    public int hashCode() { 
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Symbol)) {
            return false;
        }
        Symbol s = (Symbol) o;
        return (name.equals(s.name));
    }
}
