package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;

/**
 * An ERE representing a pattern occuring an arbitrary amount of times, i.e. at least 0.
 */
public class Kleene extends ERE {
    
    /**
     * Acquire a Kleene ERE instance.
     * @param child The pattern that can occur at least 0 times.
     * @return An instance of an ERE that matches the pattern at least 0 times.
     */
    public static ERE get(ERE child) {
        return new Kleene(child);
    }
    
    /**
     * Construct a Kleene ERE instance.
     * @param child The pattern that can occur at least 0 times.
     */
    private Kleene(ERE child) {
        children = new ArrayList<ERE>();
        children.add(child);
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.STAR;
    }
    
    @Override
    public String toString() {
        return children.get(0) + "*";
    }
    
    @Override
    public ERE copy() {
        return new Kleene(children.get(0).copy());
    }
    
    @Override
    public boolean containsEpsilon() {
        return true;
    }
    
    @Override
    public ERE derive(Symbol s) {
        return Concat.get(children.get(0).derive(s), copy());
    }
}
