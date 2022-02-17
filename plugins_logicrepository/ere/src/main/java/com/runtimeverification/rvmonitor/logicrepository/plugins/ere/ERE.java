package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import java.util.List;
import java.util.Collections;

//abstract class for the internal representation of our 
//ERE formulas
/**
 * Abstract class representing an element of an ERE formula.
 */
public abstract class ERE implements Comparable{
    protected List<ERE> children;  
    protected int hash = 0;
    
    /**
     * Retrieve the instance of the EREType enum corresponding to this class.
     * @return The related EREType instance.
     */
    public abstract EREType getEREType();
    
    /**
     * Retrieve a list of the children of this element.
     * @return The children of the element.
     */
    public List<ERE> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    /**
     * The string representation of the element (and potentially its children).
     * @return The string representation of the element.
     */
    public abstract String toString();
    
    /**
     * Syntactic comparator method for ERE elements.
     * The return values follow standard Java practices.
     * 
     * This is inherited subclasses save Symbol, True, and False.
     * @param o The object to compare the current one.
     * @return -1 := <, 0 := ==, 1 := >
     */
    public int compareTo(Object o) {
        // we want to push all non-EREs
        // to the end, in a sort.  In this project
        // o should always be an ERE, however
        if(!(o instanceof ERE)) return -1;
        ERE L = (ERE) o;
        // If this node and the node being compared
        // have different types, return the comparison
        // of those types, we are done
        if(L.getEREType() != getEREType()) {
            return getEREType().compareTo(L.getEREType());
        }
        //If, instead, the types are equal the comparison
        //must be based on the children, from left to right
        List<ERE> lChildren = L.getChildren();
        for(int i = 0; i < children.size(); ++i) {
            int res = children.get(i).compareTo(lChildren.get(i));
            if(res != 0) {
                return res;
            }
        }
        //If all children are equal these nodes must be equal
        return 0;
    }
    
    /**
     * Syntactic equality method for ERE elements.
     *
     * The idea here is similar to compare to
     * and this method is inherited by all subclasses
     * save Symbol, True, and False
     */
    public boolean equals(Object o) {
        /*
         * This could be implemented simply by checking
         * if compareTo is 0, but this method should be
         * slightly faster, and equality is important
         * for sets and maps.
         */
        if(!(o instanceof ERE)) return false;
        ERE ere = (ERE) o;
        if(ere.getEREType() != getEREType()) return false; 
        List<ERE> lChildren = ere.getChildren();
        for(int i = 0; i < children.size(); ++i) {
            if(!children.get(i).equals(lChildren.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * A unique hash code for the element, dependent on its type and its children.
     * @return The element's hash code.
     */
    public int hashCode() {
        //let's actually store the hashcode so we 
        //don't recursively compute it EVERY time
        if(hash != 0) {
            return hash;
        }
        if(children == null) {
            hash = super.hashCode();
            return hash;
        }
        hash = getEREType().toInt();
        for(ERE child : children) {
            hash ^= child.hashCode();
        }
        return hash;
    }
    
    /**
     * Produces a deep copy of the ERE node.
     * This method exists so an ERE node can be copied without knowing what type it is.
     * @return A deep copy of the ERE node.
     */
    public abstract ERE copy();
    
    /**
     * If the ERE node matches an epsilon.
     * @return If epsilon is in the set of matched elements.
     */
    public abstract boolean containsEpsilon(); 
    
    /**
     * Produce the derivative of the ERE with respect to the given symbol.
     * If the ERE matches the given alphabet symbol, then the derivative
     * is the ERE that matches anything that could have come after the
     * first character. If it does not match the given alphabet symbol,
     * then the derivative is the empty set.
     * @param s The symbol to match first.
     * @return An ERE matching everything following the symbol.
     */
    public abstract ERE derive(Symbol s);
}
