package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * abstract class for the internal representation of our 
 * LTL formulas
 */
public abstract class LTLFormula implements Comparable{
    protected ArrayList<LTLFormula> children;  
    protected int hash = 0;
    
    protected static HashMap<String, Atom> stringToRef;
    protected static HashMap<Atom, String> refToString;
    protected static True theTrue;
    protected static False theFalse;
    protected static END theEND;
    protected static Set<Atom> atoms = null;
    public static LinkedHashSet<LinkedHashSet<Atom>> sigma;
    static {
        stringToRef = new HashMap<String, Atom>();
        refToString = new HashMap<Atom, String>();
        theTrue = new True();
        theFalse = new False();
        theEND = new END();
    }
    
    public abstract LTLType getLTLType();
    public List<LTLFormula> getChildren(){
        return Collections.unmodifiableList(children);
    }
    
    /**
     * Lower should be called before normalize
     * It lowers =>, <=> and xor into the proper
     * operations, making parsers simpler
     * by not requiring them to perform lowering
     */
    protected LTLFormula lower(){
        //Java foreach constructs should really
        //allow ASSIGNMENT to the induction element.
        //Until they do they are EXTREMELY limited
        for(int i = 0; i < children.size(); ++i){
            children.set(i,children.get(i).lower());
        }
        return this;
    }
    
    /**
     * normalize places a formula into normalized form
     */
    protected LTLFormula normalize() {
        return this.normalize(false);
    }
    
    /**
     * reduce performs boolean reductions and inlines
     * nested boolean operators into their parents
     * (e.g. a and b and (b and c) becomes
     * first a and b and b and c, then
     * a and b and c
     */
    protected LTLFormula reduce(){
        for(int i = 0; i < children.size(); ++i){
            children.set(i,children.get(i).reduce());
        }
        return this;
    }
    
    /**
     * This is the public interface to the simplification
     * passes that ensures that they are called in order.
     *
     * This method is inherited by all sublcasses.  It
     * would be slightly more efficient for Atom, True,
     * and False to implemented a version which just returns,
     * but who cares about the efficiency of simplifying a
     * formula that is JUST an Atom, True, or False:
     * this method is only ever called on the top node
     */
    public LTLFormula simplify(){
        //maybe I should rename this method
        //since it also computes sigma
        sigma = SetOperations.pow(atoms().toArray(new Atom[0]));
        LTLFormula ret = lower();
        //System.out.println(ret);
        ret = ret.normalize();
        //System.out.println(ret);
        ret = ret.reduce();
        //System.out.println(ret);
        return ret;
    }
    
    /**
     * Place a formula in negative normal form.
     * WARNING -- may return a formula which shares 
     * part of the original formula.  Do not use orginal
     * formula.
     */
    protected abstract LTLFormula normalize(boolean b);
    
    public String toString(){
        if(children.size() == 1){
            return getLTLType().toString()  
            + children.get(0).toString();
        }
        String ret = "(";
        ret += children.get(0).toString();
        for(int i = 1; i < children.size(); ++i){
            ret += getLTLType().toString() + children.get(i).toString();
        }
        ret += ")";
        return ret;
    }
    
    /** 
     * SYNTACTIC compareTo of LTLFormula.
     * The return values can be thought of
     * as follows (standard Java practice)
     * -1 := <, 0 := ==, 1 := >
     *
     * this is inherited my all subclasses
     * save Atom, True, and False
     */
    public int compareTo(Object o){
        // we want to push all non-LTLFormulas
        // to the end, in a sort.  In this project
        // o should always be an LTLFormula, however
        if(!(o instanceof LTLFormula)) return -1;
        LTLFormula L = (LTLFormula) o;
        // If this node and the node being compared
        // have different types, return the comparison
        // of those types, we are done
        if(L.getLTLType() != getLTLType()) {
            return getLTLType().compareTo(L.getLTLType());
        }
        //If, instead, the types are equal the comparison
        //must be based on the children, from left to right
        List<LTLFormula> lChildren = L.getChildren();
        for(int i = 0; i < children.size(); ++i){
            int res = children.get(i).compareTo(lChildren.get(i));
            if(res != 0) return res; 
        }
        //If all children are equal these nodes must be equal
        return 0;
    }
    
    /** 
     * SYNTACTIC equality of LTLFormula.
     * This could be implemented simply by checking
     * if compareTo is 0, but this method should be
     * slightly faster, and equality is important
     * for sets and maps
     *
     * The idea here is similar to compare to
     * and this method is inherited by all subclasses
     * save Atom, True, and False
     */
    @Override
    public boolean equals(Object o){
        if(!(o instanceof LTLFormula)) return false;
        LTLFormula L = (LTLFormula) o;
        if(L.getLTLType() != getLTLType()) return false; 
        List<LTLFormula> lChildren = L.getChildren();
        for(int i = 0; i < children.size(); ++i){
            if(!children.get(i).equals(lChildren.get(i))){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode(){
        //let's actually store the hashcode so we 
        //don't recursively compute it EVERY time
        if(hash != 0) {
            return hash;
        }
        if(children == null) {
            hash = super.hashCode();
            return hash;
        }
        hash = getLTLType().toInt();
        for(LTLFormula child : children){
            hash ^= child.hashCode();
        }
        return hash;
    }
    
    /**
     * This returns a copy of a given LTLFormula. I prefer
     * the explicit word copy to a copy constructor, which
     * is often less clear
     */
    public abstract LTLFormula copy();
    
    public final LinkedHashSet<LTLFormula> subFormulae() {
        LinkedHashSet<LTLFormula> ret = new LinkedHashSet<LTLFormula>();
        subFormulae(ret);
        return ret;
    }
    
    
    /** 
     * Return the subformula of a formula (inculding itself)
     * in a bottom up ordering.
     *
     * This is inherited by all subclasses save
     * Atom, True, and False, which simply add themselves to the
     * set.
     */
    public void subFormulae(LinkedHashSet acc){
        for(LTLFormula child : children){
            child.subFormulae(acc); 
        }
        acc.add(this);
    }
    
    /**
     * Return the atoms of a given formula.
     * Note that this returns atoms that have disappeared
     * do to simplifcation.  This is the behavior we
     * want because the generated atomaton must still
     * consider said atoms, from a user's perspective
     *
     * This is inherited by all sub classes
     */
    public final Set<Atom> atoms(){
        if(atoms == null) atoms = refToString.keySet();
        return atoms;
    }
    
    /**
     * Transform a formula to set form.
     *
     * This is inherited by all subclasses save
     * And and Or
     */
    public LinkedHashSet<LinkedHashSet<LTLFormula>> toSetForm(){
        LinkedHashSet<LinkedHashSet<LTLFormula>> ret 
            = new LinkedHashSet<LinkedHashSet<LTLFormula>>();
        LinkedHashSet<LTLFormula> singleton = new LinkedHashSet<LTLFormula>();
        singleton.add(this);
        ret.add(singleton);
        return ret;
    }
    
    public ATransition d(HashMap<LTLFormula, ATransition> D) {
        assert false : getLTLType().toString() + " does not support d";
        return null;
    }
    
}
