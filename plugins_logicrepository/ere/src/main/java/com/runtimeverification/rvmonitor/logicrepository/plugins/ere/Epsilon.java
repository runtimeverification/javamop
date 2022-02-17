package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

/**
 * An ERE that matches only the empty string.
 */
public class Epsilon extends ERE {
    
    private static Epsilon epsilon = new Epsilon();
    
    /**
     * Private constructor, as this is a singleton
     */
    private Epsilon() {
        
    }
    
    /**
     * Acquire an instance of the Epsilon ERE.
     * @return An instance of the Epsilon ERE.
     */
    static public Epsilon get() {
        return epsilon;
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.EPS;
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
        return EREType.EPS.compareTo(E.getEREType());
    }
    
    @Override
    public ERE copy() {
        return this;
    }
    
    @Override
    public String toString() {
        return "epsilon";
    }
    
    @Override
    public boolean containsEpsilon() {
        return true;
    }
    
    @Override
    public ERE derive(Symbol s) {
        return Empty.get();
    }
}
