package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

public class GBATransition {
    public LinkedHashSet<LTLFormula> previous;
    public LinkedHashSet<LTLFormula> now;
    public LinkedHashSet<LTLFormula> next;
    public LinkedHashSet<LinkedHashSet<Atom>> symbols;
    
    GBATransition(LinkedHashSet<LTLFormula> previous,
                  LinkedHashSet<LTLFormula> now,
                  LinkedHashSet<LTLFormula> next,
                  LinkedHashSet<LinkedHashSet<Atom>> symbols
    ){
        this.previous = previous;
        this.now = now;
        this.next = next;
        this.symbols = symbols;
    }
    
    public boolean equals(Object o){
        if(!(o instanceof GBATransition)) return false;
        GBATransition comp = (GBATransition) o;
        return previous.equals(comp.previous) && now.equals(comp.now)
        && next.equals(comp.next) && symbols.equals(comp.symbols);
    }
    
    public int hashCode(){
        return previous.hashCode() ^ now.hashCode()
        ^ next.hashCode() ^ symbols.hashCode();
    }
    
    public String toString(){
        return "\n(" + previous.toString() + ", " 
        + now.toString() + ", "
        + next.toString() + ", "
        + symbols.toString() + ")"; 
    }
    
}
