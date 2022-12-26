package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

public class GBAState {
    public LinkedHashSet<LTLFormula> first;
    public LinkedHashSet<LTLFormula> second;
    
    GBAState(LinkedHashSet<LTLFormula> first,LinkedHashSet<LTLFormula> second){
        this.first = first;
        this.second = second;
    }
    
    public String toString(){
        return "(" + first.toString() + ", " + second.toString() + ")"; 
    }
    
    public boolean equals(Object o){
        if(!(o instanceof GBAState)) return false;
        GBAState comp = (GBAState) o;
        return first.equals(comp.first) && second.equals(comp.second);
    }
    
    public int hashCode(){
        return first.hashCode() ^ second.hashCode();
    }
    
}
