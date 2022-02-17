package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

/**
 * An enumeration of the different LTLFormula types.
 */
public enum LTLType {
    // ordering is important to compare LTLFormula
    //we want true false and atoms to be sorted to the
    //leftmost in AND, OR, XOR nodes for optimization
    //purposes (inlining pass)
    T("t", 1),   //True
    F("f", 10),   //False
    A("atom", 15),   //atom
    NEG("not ", 20), //negation
    AND(" and ", 25), //and
    XOR(" xor ", 30), //xor
    IMP(" => ", 35), //implication
    IFF(" <=> ", 40), //double implication
    OR(" or ", 45),  //or
    U(" U ", 50),   //until
    DU(" ~U ", 55),  //dual of until
    S(" S ", 60),   //since
    DS(" ~S ", 65),  //dual of since
    X("o ", 70),   //next
    DX("~o ", 75),  //dual of next
    Y("(*) ", 80),   //previously (yesterday)
    DY("~(*) ", 85),  //dual of previously
    END("END", 90); //END
    
    private String stringRepresentation;
    private int intRepresentation;
    
    /**
     * Construct the LTLType enum instance.
     * @param s The string representation of the type.
     * @param i The integer representation of the type.
     */
    private LTLType(String s, int i) {
        stringRepresentation = s;
        intRepresentation = i;
    }
    
    /**
     * The string representation of the enum.
     * @return A string representing the instance.
     */
    public String toString(){
        return stringRepresentation;
    }
    
    /**
     * The integer representation of the enum.
     * @return A string representation of the instance.
     */
    public int toInt(){
        return intRepresentation;
    }
    
}
