package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;

/**
 * class representing an Implication node in an LTL formula
 */
public class Implication extends LTLFormula {
    
    /**
     * Construct a Implication node around the given children.
     * @param leftChild The left node to construct a Implication around.
     * @param rightChild The right node to construct a Implication around.
     */
    public Implication(LTLFormula leftChild, LTLFormula rightChild){
        children = new ArrayList<LTLFormula>(2);
        children.add(leftChild);
        children.add(rightChild);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.IMP;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        assert false : "normalize called before lowering!";
        return null;
    }
    
    @Override
    protected LTLFormula lower(){
        for(int i = 0; i < children.size(); ++i){
            children.set(i,children.get(i).lower());
        }
        ArrayList<LTLFormula> c = new ArrayList<LTLFormula>(2);
        c.add(new Negation(children.get(0)));
        c.add(children.get(1));
        return new Or(c); 
    }
    
    @Override
    public LTLFormula copy(){
        return new Implication(children.get(0).copy(), children.get(1).copy());
    }
}
