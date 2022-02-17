package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;

/**
 * An ERE representing the negation of another ERE.
 */
public class Negation extends ERE {
    
    /**
     * Acquire an instance of a negation of another ERE pattern.
     * @param child The ERE to negate
     * @return An ERE that negates the given ERE.
     */
    public static ERE get(ERE child) {
        if(child.getEREType() == EREType.NEG) return child.children.get(0);
        return new Negation(child);
    }
    
    /**
     * Construct a negation of another ERE.
     * @param child The ERE to negate.
     */
    private Negation(ERE child) {
        children = new ArrayList<ERE>();
        children.add(child);
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.NEG;
    }
    
    @Override
    public String toString() {
        return "~(" + children.get(0) + ")";
    }
    
    @Override
    public ERE copy() {
        return new Negation(children.get(0).copy());
    }
    
    @Override
    public boolean containsEpsilon() {
        return !(children.get(0).containsEpsilon());
    }
    
    @Override
    public ERE derive(Symbol s) {
        return Negation.get(children.get(0).derive(s));
    }
}
