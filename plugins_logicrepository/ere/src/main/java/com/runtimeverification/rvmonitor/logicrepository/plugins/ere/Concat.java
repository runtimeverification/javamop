package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;

/**
 * An extended regular expression element represeting two elements in sequence.
 */
public class Concat extends ERE {
    
    /**
     * Acquire an extended regular expression element representing a sequence of two elements.
     * @param left The element to match first.
     * @param right The element to match second.
     * @return An element that matches the left parameter then the right parameter.
     */
    public static ERE get(ERE left, ERE right) {
        Concat cat = new Concat(left, right);
        ERE ret = cat.simplify();
        return ret;
    }
    
    /**
     * Construct a Concat element.
     * @param left The element to match first.
     * @param right The element to match second.
     */
    private Concat(ERE left, ERE right) {
        children = new ArrayList<ERE>(2);
        children.add(left);
        children.add(right);
    }
    
    /**
     * Shallow simplification of the Concat ERE element.
     * @return A possibly simpler ERE matching the same expressions.
     */
    private ERE simplify(){
        if(children.get(0) == Empty.get()) {
            return Empty.get();
        } else if(children.get(1) == Empty.get()) {
            return Empty.get();
        } else if(children.get(0) == Epsilon.get()) {
            return children.get(1);
        } else if(children.get(1) == Epsilon.get()) {
            return children.get(0);
        }
        return this;
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.CAT;
    }
    
    @Override
    public String toString() {
        return "(" + children.get(0) + " " + children.get(1) + ")";
    }
    
    @Override
    public ERE copy() {
        return new Concat(children.get(0).copy(), children.get(1).copy());
    }
    
    @Override
    public boolean containsEpsilon() {
        for(ERE child : children) {
            if(!child.containsEpsilon()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ERE derive(Symbol s){
        ERE left = children.get(0);
        ERE right = children.get(1);
        if(left.containsEpsilon()) {
            ArrayList<ERE> orChildren = new ArrayList<ERE>(2);
            orChildren.add(Concat.get(left.derive(s), right.copy()));
            orChildren.add(right.derive(s));
            return Or.get(orChildren);
        }
        return Concat.get(left.derive(s), right.copy());
    }
}
