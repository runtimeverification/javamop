package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

/**
 * The different types of ERE elements
 */
public enum EREType {
    EMP("empty", 100000),  //empty
    EPS("epsilon", 100000),  //epsilon
    S("symbol", 1),    //atom
    NEG("~", 10),  //negation
    CAT("cat", 100),  //concat
    STAR("*", 1000), //kleene closure
    OR("|", 10000);   //or
    
    private final String stringRep;
    private final int intRep;
    
    /**
     * Internal constructor initializing the string and integer representations.
     * @param stringRep The string representation of this type.
     * @param intRep The integer representation of this type.
     */
    private EREType(String stringRep, int intRep) {
        this.stringRep = stringRep;
        this.intRep = intRep;
    }
    
    /**
     * String representation of the ERE type.
     * @return A string unique to the type.
     */
    public String toString() {
        return stringRep;
    }
    
    /**
     * Integer representation of the ERE type.
     * Used for constructing hash codes of ERE elements.
     * @return An int unique to the type.
     */
    public int toInt() {
        return intRep;
    }
}
