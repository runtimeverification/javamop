package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.HashMap;

//class representing a True node in an LTL formula
public class True extends LTLFormula {
    
    /**
     * Retrieve the singleton instance of the True object.
     * @return The True instance.
     */
    static public True get(){
        return LTLFormula.theTrue;
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.T;
    }
    
    @Override
    protected LTLFormula lower(){
        return this;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) return False.get();
        else  return this;
    }
    
    @Override
    protected LTLFormula reduce(){
        return this;
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
        return "true";
    }
    
    @Override
    public void subFormulae(LinkedHashSet acc){
        acc.add(this);
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>(1);
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        
        retTuples.add(new ATuple(empty, sigma, empty));
        return new ATransition(retTuples);
    }
}
