package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

/**
 * An ERE that matches the empty set, i.e. does not match any strings.
 */
public class Empty extends ERE {
    
    private static Empty empty = new Empty();
    
    /**
     * Private constructor, as this class is a singleton.
     */
    private Empty() {
        
    }
    
    /**
     * Acquire an instance of the Empty ERE.
     * @return an instance of the Empty ERE.
     */
    static public Empty get() {
        return empty;
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.EMP;
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
        return EREType.EMP.compareTo(E.getEREType());
    }
    
    @Override
    public ERE copy() {
        return this;
    }
    
    @Override
    public String toString() {
        return "empty";
    }
    
    @Override
    public boolean containsEpsilon() {
        return false;
    }
    
    @Override
    public ERE derive(Symbol s) {
        return empty;
    }
}
