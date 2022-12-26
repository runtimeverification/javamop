package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * An ERE that matches any out of a set of options.
 */
public class Or extends ERE {
    /**
     * Acquire an instance of an ERE that matches any of the given elements.
     * @param children Any of the elements that can be matched.
     * @return An ERE matching any of the children elements.
     */
    static public ERE get(List<ERE> children) {
        Or or = new Or(children);
        ERE ret = or.simplify();
        return ret;
    }
    
    /**
     * Construct an ERE that matches any of the children.
     * @param children Any child ERE that can be matched.
     */
    private Or(List<ERE> children) {
        assert children != null && children.size() >= 2 
        : "Or requires at least two children!";
        this.children = children;
    }
    
    /**
     * Perform a shallow simplification of the element.
     */
    public ERE simplify() {
        //Flatten any child Or elements into this one.
        ArrayList<ERE> flattened = new ArrayList<ERE>(children.size() >> 1);
        ArrayList<ERE> previous = new ArrayList<ERE>(children);
        boolean changed = true;
        while(changed) {
            changed = false; 
            flattened = new ArrayList<ERE>(children.size());
            for(ERE child : previous) {
                if(child.getEREType() == EREType.OR) {
                    flattened.addAll(child.getChildren());
                    changed = true;
                } else {
                    flattened.add(child); 
                }
            }
            previous = flattened;
        }
        children = flattened;
        // Sort the elements so that structurally equal one will be adjacent.
        Collections.sort(children);
        // Remove elements that do not match anything.
        for(int i = 0; i < children.size(); ++i) {
            if(children.get(i) == Empty.get()) {
                children.remove(i);
            }
        }
        // Remove duplicate elements.
        for(int i = 0; i < children.size() - 1; ++i) {
            if(children.get(i).equals(children.get(i + 1))) {
                children.remove(i);
                i--;
            }
        }
        // Simplify degenerate Or instances to simpler ERE types.
        if(children.size() == 0) {
            return Empty.get();
        }
        else if(children.size() == 1) {
            return children.get(0);
        }
        return this;
    }
    
    @Override
    public EREType getEREType() { 
        return EREType.OR;
    }
    
    @Override
    public String toString() {
        String ret = "(" + children.get(0);
        for(int i = 1; i < children.size(); ++i) {
            ret += " | " + children.get(i);
        }
        ret += ")";
        return ret;
    }
    
    @Override
    public ERE copy() {
        ArrayList<ERE> retChildren = new ArrayList<ERE>(children.size());
        for(ERE child : children) {
            retChildren.add(child.copy());
        }
        return new Or(retChildren);
    }
    
    @Override
    public boolean containsEpsilon() {
        for(ERE child : children) {
            if(child.containsEpsilon()) {
                return true;
            }
        }
        return false;
    }
    @Override
    public ERE derive(Symbol s) {
        ArrayList<ERE> orChildren = new ArrayList<ERE>(children.size());
        for(ERE child : children) {
            orChildren.add(child.derive(s));
        }
        return Or.get(orChildren);
    }
}
