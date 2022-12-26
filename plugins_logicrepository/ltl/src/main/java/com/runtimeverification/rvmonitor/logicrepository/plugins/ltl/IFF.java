package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;

/**
 * class representing an If And Only If node in an LTL formula
 */
public class IFF extends LTLFormula {
    
    /**
     * Construct a If And Only If node around the given children.
     * @param leftChild The left node to construct a If And Only If around.
     * @param rightChild The right node to construct a If And Only If around.
     */
    public IFF(LTLFormula leftChild, LTLFormula rightChild){
        children = new ArrayList<LTLFormula>(2);
        children.add(leftChild);
        children.add(rightChild);
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.IFF;
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
        ArrayList<LTLFormula> leftChildren = new ArrayList<LTLFormula>(2);
        ArrayList<LTLFormula> rightChildren = new ArrayList<LTLFormula>(2);
        ArrayList<LTLFormula> nextChildren = new ArrayList<LTLFormula>(2);
        leftChildren.add(new Negation(children.get(0).copy()));
        leftChildren.add(new Negation(children.get(1).copy()));
        rightChildren.add(children.get(0));
        rightChildren.add(children.get(1));
        nextChildren.add(new And(leftChildren));
        nextChildren.add(new And(rightChildren));
        return new Or(nextChildren); 
    }
    
    @Override
    public LTLFormula copy(){
        return new IFF(children.get(0).copy(), children.get(1).copy());
    }
}
