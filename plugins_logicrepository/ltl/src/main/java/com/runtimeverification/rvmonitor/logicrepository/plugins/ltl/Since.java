package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

/**
 * class representing an Since node in an LTL formula
 */
public class Since extends LTLFormula {
    
    public Since(LTLFormula leftChild, LTLFormula rightChild){
        children = new ArrayList<LTLFormula>(2);
        children.add(leftChild);
        children.add(rightChild);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.S;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) {
            return new DualSince(
                new Negation(children.get(0)).normalize(false),
                                 new Negation(children.get(1)).normalize(false));
        }
        else{
            children.set(0,children.get(0).normalize(false));
            children.set(1,children.get(1).normalize(false));
            return this;
        }
    }
    
    @Override
    public LTLFormula copy(){
        return new Since(children.get(0).copy(), children.get(1).copy());
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>();
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        LinkedHashSet<LTLFormula> previous  
        = new LinkedHashSet<LTLFormula>(1);
        LinkedHashSet<ATuple> previousTuples 
        = new LinkedHashSet<ATuple>(1);
        
        previous.add(this);
        previousTuples.add(new ATuple(previous, sigma, empty));
        
        retTuples.addAll(D.get(children.get(1)).tuples);
        retTuples.addAll(D.get(children.get(0)).and(new ATransition(previousTuples)).tuples);
        return new ATransition(retTuples);
    }
    
}
