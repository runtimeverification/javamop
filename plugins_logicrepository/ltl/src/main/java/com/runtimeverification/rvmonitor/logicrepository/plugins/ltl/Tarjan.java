package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

// This is Tarjan's strongly connected component algorithm 
// for Buchi Automata implemented as the BA class
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Tarjan {
    private static int i;
    private static HashSet<BAState> stackContains;
    private static ArrayList<BAState> stack;
    private static HashMap<BAState, Integer> indices;
    private static HashMap<BAState, Integer> lowLinks;
    private static LinkedHashSet<LinkedHashSet<BAState>> output;
    
    public static LinkedHashSet<LinkedHashSet<BAState>>
    SCC(LinkedHashMap<BAState, BATransition> T, LinkedHashSet<BAState> Q){
        i = 0;
        stack = new ArrayList();  
        stackContains = new HashSet();  
        indices = new HashMap();
        lowLinks = new HashMap();
        output = new LinkedHashSet();
        for(BAState s : Q){
            if(!indices.containsKey(s)){
                tarjan(T, s);
            }
        }
        return output;
    }
    
    private static void tarjan(LinkedHashMap<BAState, BATransition> T, BAState s){
        indices.put(s, i);
        lowLinks.put(s, i++);
        stack.add(s);
        stackContains.add(s);
        BATransition trans =  T.get(s);
        if(trans != null){
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : trans.keySet()){
                //We use keySet and lookup rather than values because there may
                //be duplicates in values
                for(BAState dest : trans.get(symbols)){
                    if(!indices.containsKey(dest)){
                        tarjan(T,dest);
                        lowLinks.put(s, Math.min(lowLinks.get(s), lowLinks.get(dest)));
                    }
                    else if(stackContains.contains(dest)){
                        lowLinks.put(s, Math.min(lowLinks.get(s), indices.get(dest)));
                    }
                }
            }
        }
        if(lowLinks.get(s) == indices.get(s)){ //s is the root of an SCC
            LinkedHashSet<BAState> inner = new LinkedHashSet();
            
            BAState top = null;
            do {
                int ti = stack.size() - 1;
                top = stack.get(ti); 
                stack.remove(ti); 
                stackContains.remove(top);
                inner.add(top);
            } while(top != s);
            output.add(inner);
        }
    }
    
    public static boolean isIsolated(
        LinkedHashMap<BAState, BATransition> T, 
        LinkedHashSet<BAState> SCC) {
        for(BAState s : SCC){
            BATransition trans =  T.get(s);
            if(trans != null){
                for(LinkedHashSet<LinkedHashSet<Atom>> symbols : trans.keySet()){
                    for(BAState dest : trans.get(symbols)){
                        if(!SCC.contains(dest)) return false; 
                    }
                }
            }
        }
    return true;
    }
    
    public static boolean isTotal(
        LinkedHashMap<BAState, BATransition> T, 
        LinkedHashSet<BAState> SCC,
        LinkedHashSet<LinkedHashSet<Atom>> sigma) {
        for(BAState s : SCC){
            BATransition trans = T.get(s);
            if(trans == null) return false;
            for(LinkedHashSet<Atom> atoms : sigma){      
                boolean total = false;
                for(BAState dest : trans.getSat(atoms)){
                    if(SCC.contains(dest)) {
                        total = true;
                        break;
                    }
                }
                if(!total) return false;
            }
        }
        return true;
    }
}
