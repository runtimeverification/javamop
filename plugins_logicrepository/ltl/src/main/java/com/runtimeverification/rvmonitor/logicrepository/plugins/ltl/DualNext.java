package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

/**
 * class representing an DualNext node in an LTL formula
 */
public class DualNext extends LTLFormula {
    
    /**
     * Construct a DualNext node around the given child.
     * @param child The node to construct a DualNext around.
     */
    public DualNext(LTLFormula child){
        children = new ArrayList<LTLFormula>(1);
        children.add( child);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.DX;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) {
            return new Next(
                new Negation(children.get(0)).normalize(false));
        }
        else{
            children.set(0,children.get(0).normalize(false));
            return this;
        }
    }
    
    @Override
    public LTLFormula copy(){
        return new DualNext(children.get(0).copy());
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>(1);
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        LinkedHashSet<LinkedHashSet<LTLFormula>> nextSet
        = children.get(0).toSetForm();
        LinkedHashSet<LTLFormula> ENDnext  
        = new LinkedHashSet<LTLFormula>(1);
        ENDnext.add(END.get());
        
        for(LinkedHashSet<LTLFormula> next : nextSet){
            retTuples.add(new ATuple(empty, sigma, next));
        }
        retTuples.add(new ATuple(empty, sigma, ENDnext)); 
        return new ATransition(retTuples);
    }
}
