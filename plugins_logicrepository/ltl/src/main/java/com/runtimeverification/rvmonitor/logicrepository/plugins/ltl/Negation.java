package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * class representing a negation node in an LTL formula
 */
public class Negation extends LTLFormula {
    
    /**
     * Construct a negation around the given element.
     * @param child The element to build a negation around.
     */
    public Negation(LTLFormula child){
        children = new ArrayList<LTLFormula>(1);
        children.add(child);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.NEG;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        LTLFormula child = children.get(0);
        //The is a negation above, simply remove this
        //negation
        if(b) return child.normalize(false);
        //This is a mild optimization to reduce the 
        //number of Negation nodes created
        //The other option would be to return 
        //children[0].normalize no matter what
        //but this would create an extra Negation
        //on Atoms
        if(child.getLTLType() == LTLType.A) return this; 
        //There is no negation above, so negate
        //child, and child is NOT an Atom
        return child.normalize(true); 
    }
    
    @Override
    public LTLFormula copy(){
        LTLFormula copied = children.get(0).copy();
        return new Negation(copied);
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>(1);
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        
        // We want all the letters in sigma which do not contain this
        // atom, which is half of sigma
        LinkedHashSet<LinkedHashSet<Atom>> natomSigma
        = new LinkedHashSet<LinkedHashSet<Atom>>(sigma.size() >> 1);
        
        assert children.get(0).getLTLType() == LTLType.A 
        : "computing automaton from formula not in negative normal form!";
        Atom child = (Atom) children.get(0);
        for(LinkedHashSet<Atom> letter : sigma){
            if(!letter.contains(child)){
                natomSigma.add(letter);
            }
        }
        
        retTuples.add(new ATuple(empty, natomSigma, empty));
        return new ATransition(retTuples);
    }
}
