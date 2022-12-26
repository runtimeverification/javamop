package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.HashMap;

/**
 * 
 */
public class Symbol extends ERE {
    
    private static final HashMap<String, Symbol> stringToRef = new HashMap<String, Symbol>();
    private static final HashMap<Symbol, String> refToString = new HashMap<Symbol, String>();
    
    /**
     * Private constructor, as instances of this are managed by the {@link get(String)} method.
     */
    private Symbol() {
        
    }
    
    /**
     * Acquire a Symbol ERE for the given string.
     * Each string symbol name is associated with a specific instance of the Symbol class.
     * These instances can be reference-compared for equality or inequality.
     * @param name The name of the symbol.
     * @return An instance of Symbol unique to its name.
     */
    static public Symbol get(String name) {
        Symbol self = stringToRef.get(name); 
        if(self != null) {
            return self;
        }
        Symbol ret = new Symbol();
        stringToRef.put(name, ret);
        refToString.put(ret, name);
        return ret;
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.S;
    }
    
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
    
    @Override
    public int compareTo(Object o) {
        if(!(o instanceof ERE)) {
            return -1;
        }
        ERE E = (ERE) o;
        if(E.getEREType() == EREType.S) {
            return refToString.get(this).compareTo(refToString.get(o));
        }
        return EREType.S.compareTo(E.getEREType());
    }
    
    @Override
    public ERE copy() {
        return this;
    }
    
    @Override
    public String toString() {
        return refToString.get(this);
    }
    
    @Override
    public boolean containsEpsilon() {
        return false;
    }
    
    @Override
    public ERE derive(Symbol s) {
        if(this == s) {
            return Epsilon.get();
        }
        return Empty.get();
    }
}
