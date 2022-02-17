package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

/**
 * class representing an DualSince node in an LTL formula
 */
public class DualSince extends LTLFormula {
    
    /**
     * Construct a DualSince node around the given children.
     * @param leftChild The left node to construct a DualSince around.
     * @param rightChild The right node to construct a DualSince around.
     */
    public DualSince(LTLFormula leftChild, LTLFormula rightChild){
        children = new ArrayList<LTLFormula>(2);
        children.add(leftChild);
        children.add(rightChild);
    }
    
    public LTLType getLTLType(){ 
        return LTLType.DS;
    }
    
    protected LTLFormula normalize(boolean b) {
        if(b) {
            return new Since(
                new Negation(children.get(0)).normalize(false),
                             new Negation(children.get(1)).normalize(false));
        }
        else{
            children.set(0,children.get(0).normalize(false));
            children.set(1,children.get(1).normalize(false));
            return this;
        }
    }
    
    public LTLFormula copy(){
        return new DualSince(children.get(0).copy(), children.get(1).copy());
    }
    
    //This goes against the formulation in the paper
    //we add the Until ndoe and the End to the same
    //tuple...because it is equivalent and uses less memory
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>();
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        LinkedHashSet<LTLFormula> previous  
        = new LinkedHashSet<LTLFormula>(1);
        LinkedHashSet<LTLFormula> ENDprevious  
        = new LinkedHashSet<LTLFormula>(1);
        
        
        retTuples.addAll(D.get(children.get(0)).tuples);
        previous.add(this);
        ENDprevious.add(END.get());
        retTuples.add(new ATuple(previous, sigma, empty));
        retTuples.add(new ATuple(ENDprevious, sigma, empty));
        
        return new ATransition(retTuples).and(D.get(children.get(1)));
    }
    
}
