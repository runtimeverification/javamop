package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

/**
 * represents the END state in an Alternating Automaton, should
 * never appear in an actual formula
 */
public class END extends LTLFormula {
    
    /**
     * Return the singleton instance of the END class.
     * @return The END instance.
     */
    static public END get(){
        return LTLFormula.theEND;
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.END;
    }
    
    @Override
    protected LTLFormula lower(){
        assert false : "cannot lower END!";
        return null;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        assert false : "cannot normalize END!";
        return null;
    }
    
    @Override
    protected LTLFormula reduce(){
        assert false : "cannot reduce END!";
        return null;
    }
    
    @Override
    public boolean equals(Object o){
        return this == o;
    }
    
    @Override
    public int compareTo(Object o){
        if(!(o instanceof LTLFormula)) return -1;
        LTLFormula L = (LTLFormula) o;
        return LTLType.T.compareTo(L.getLTLType());
    }
    
    @Override
    public LTLFormula copy() {
        return this;
    }
    
    @Override
    public String toString(){
        return "END";
    }
    
    @Override
    public void subFormulae(LinkedHashSet acc){
        assert false : "END cannot be or have sub formulae!";
    }
}
