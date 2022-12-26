package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.HashMap;

/**
 * class representing a False node in an LTL formula
 */
public class False extends LTLFormula {
    
    /**
     * Return the singleton instance of the False class.
     * @return The single False instance.
     */
    static public False get(){
        return LTLFormula.theFalse;    
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.F;
    }
    
    @Override
    protected LTLFormula lower(){
        return this;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) return True.get();
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
        return LTLType.F.compareTo(L.getLTLType());
    }
    
    @Override
    public LTLFormula copy(){
        return this;
    }
    
    @Override
    public String toString(){
        return "false";
    }
    
    @Override
    public void subFormulae(LinkedHashSet acc){
        acc.add(this);
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples = new LinkedHashSet<ATuple>(0);
        return new ATransition(retTuples);
    }
}
