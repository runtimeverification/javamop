package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * class representing a atom node in an LTL formula
 */
public class Atom extends LTLFormula {
    //  public String name;
    
    /**
     * Construct a new Atom. This is private to make sure Atom nodes are interned correctly.
     */
    private Atom() {
        
    }
    
    /**
     * Retrieve the Atom node with the given name. This ensures that Atom nodes are interned
     * correctly.
     * @param name The name of the Atom.
     * @return The Atom carrying that name.
     */
    static public Atom get(String name){
        Atom self = LTLFormula.stringToRef.get(name); 
        if(self != null) return self;
        Atom ret = new Atom();
        stringToRef.put(name, ret);
        refToString.put(ret, name);
        return ret;
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.A;
    }
    
    @Override
    protected LTLFormula lower(){
        return this;
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        //this only evaluates to true if
        //this is a direct child of a boolean
        //op that is being DeMorgan'd
        if(b) return new Negation(this);
        return this;
    }
    
    @Override
    protected LTLFormula reduce(){
        return this;
    }
    
    @Override
    public boolean equals(Object o){
        return this == o;
    }
    
    @Override
    public int compareTo(Object o){
        if(!(o instanceof LTLFormula)) return -1;
        LTLFormula L = (LTLFormula) o;
        if(L.getLTLType() == LTLType.A) {
            if(this == L) return 0;
            if(this.hashCode() < L.hashCode()) return -1;
            if(this.hashCode() > L.hashCode()) return 1;
        }
        return LTLType.A.compareTo(L.getLTLType());
    }
    
    @Override
    public LTLFormula copy(){
        return this;
    }
    
    @Override
    public String toString(){
        return LTLFormula.refToString.get(this);
    }
    
    @Override
    public void subFormulae(LinkedHashSet acc){
        acc.add(this);
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        LinkedHashSet<ATuple> retTuples 
        = new LinkedHashSet<ATuple>(1);
        LinkedHashSet<LTLFormula> empty  
        = new LinkedHashSet<LTLFormula>(0);
        
        // We want all the letters in sigma which contain this
        // atom, which is half of sigma
        LinkedHashSet<LinkedHashSet<Atom>> atomSigma
        = new LinkedHashSet<LinkedHashSet<Atom>>(sigma.size() >> 1);
        
        for(LinkedHashSet<Atom> letter : sigma){
            if(letter.contains(this)){
                atomSigma.add(letter);
            }
        }
        
        retTuples.add(new ATuple(empty, atomSigma, empty));
        return new ATransition(retTuples);
    }
}
