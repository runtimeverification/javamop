package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class BATransition 
extends LinkedHashMap<LinkedHashSet<LinkedHashSet<Atom>>,LinkedHashSet<BAState>> {
    
    public void put(LinkedHashSet<LinkedHashSet<Atom>> symbols, BAState s){
        //do not add transitions on the empty set
        if(symbols.isEmpty()) return;
        if(containsKey(symbols)){
            get(symbols).add(s);
        }
        else{
            LinkedHashSet<BAState> set = new LinkedHashSet();
            set.add(s);
            put(symbols, set);
        }
    }
    
    //This method gets any transition that has symbols which satisfy that the
    //presented atom set is true,  e.g. if the atom set is [a,b], 
    //[[], [a], [b], [a,b]] satisfies that [a,b] is true
    public LinkedHashSet<BAState> getSat(LinkedHashSet<Atom> atoms){
        LinkedHashSet<BAState> ret = new LinkedHashSet();
        for(LinkedHashSet<LinkedHashSet<Atom>> symbols : keySet()){
            if(symbols.contains(atoms)){
                ret.addAll(get(symbols));
            }
        }
        return ret;
    }
    
    public String toString(){
        String ret = "[";
        for(LinkedHashSet<LinkedHashSet<Atom>> symbols : keySet()){
            ret += "\n" + symbols + " = " + get(symbols);  
        }
        return ret + "\n]\n";
    }
}
