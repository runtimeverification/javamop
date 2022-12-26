package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

public class ATuple {
    public LinkedHashSet<LTLFormula> previous;
    public LinkedHashSet<LinkedHashSet<Atom>> symbols;
    public LinkedHashSet<LTLFormula> next;
    
    ATuple( LinkedHashSet<LTLFormula> previous,
            LinkedHashSet<LinkedHashSet<Atom>> symbols,
            LinkedHashSet<LTLFormula> next ){
        this.previous = previous;
        this.symbols = symbols;
        this.next = next;
    }
    
    /**
     * this implements (X1 union X2, s1 intersect s2, X3 union X4)
     * for tuples (X1, s1, X3) and (X2, s2, X4)
     * it must be non-destructive due to the implementation of
     * and at the ATransition level (which is essentially a cross product
     * with ATuple and as the operator crossed over rather than tuple
     * construction)
     */
    public ATuple and(ATuple second){
        LinkedHashSet<LTLFormula> retPrevious 
            = new LinkedHashSet<LTLFormula>(previous.size() + second.previous.size()) ;
        
        LinkedHashSet<LinkedHashSet<Atom>> retSymbols = null;
        
        LinkedHashSet<LTLFormula> retNext
            = new LinkedHashSet<LTLFormula>(next.size() + second.next.size()) ;
        
        retPrevious.addAll(previous);
        retPrevious.addAll(second.previous);
        retSymbols = SetOperations.intersect(symbols, second.symbols);
        retNext.addAll(next);
        retNext.addAll(second.next);
        return new ATuple(retPrevious, retSymbols, retNext);
    }
    
    public String toString(){
        return "(" + previous + ", " + symbols + ", " + next + ")\n";
    }
}
