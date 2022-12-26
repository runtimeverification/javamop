package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

public class BAState {
    public LinkedHashSet<LTLFormula> first;
    public LinkedHashSet<LTLFormula> second;
    public int r;
    
    BAState(LinkedHashSet<LTLFormula> first,LinkedHashSet<LTLFormula> second, int r){
        this.first = first;
        this.second = second;
        this.r = r;
    }
    
    public String toString(){
        return "(" + first.toString() + ", " + second.toString() + "," + r + ")"; 
    }
    
    public boolean equals(Object o){
        if(!(o instanceof BAState)) return false;
        BAState comp = (BAState) o;
        return first.equals(comp.first) && second.equals(comp.second)
        && r == comp.r;
    }
    
    public int hashCode(){
        return first.hashCode() ^ second.hashCode() ^ r;
    }
}
