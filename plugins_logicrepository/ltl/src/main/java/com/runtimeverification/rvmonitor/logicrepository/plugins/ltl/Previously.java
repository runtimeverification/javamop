package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

/**
 * class representing an Previously node in an LTL formula
 */
public class Previously extends LTLFormula {
    
    /**
     * 
     */
    public Previously(LTLFormula child){
        children = new ArrayList<LTLFormula>(1);
        children.add(child);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.Y;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) {
            return new DualPreviously(
                new Negation(children.get(0)).normalize(false));
        }
        else{
            children.set(0,children.get(0).normalize(false));
            return this;
        }
    }
    
    @Override
    public LTLFormula copy(){
        return new Previously(children.get(0).copy());
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>(1);
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        LinkedHashSet<LinkedHashSet<LTLFormula>> previousSet
        = children.get(0).toSetForm();
        
        for(LinkedHashSet<LTLFormula> previous : previousSet){
            retTuples.add(new ATuple(previous, sigma, empty));
        }
        return new ATransition(retTuples);
    }
    
}
