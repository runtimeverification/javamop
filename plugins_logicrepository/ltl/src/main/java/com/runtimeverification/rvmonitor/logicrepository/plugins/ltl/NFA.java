package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.LinkedHashMap;

public class NFA {
    private LinkedHashMap<Integer, NFATransition> transitions =
        new LinkedHashMap<Integer, NFATransition>();
    public LinkedHashSet<Integer> I;
    public LinkedHashSet<LinkedHashSet<Atom>> sigma;
    
    NFA(BA ba){
        sigma = ba.sigma;
        LinkedHashSet<LinkedHashSet<BAState>> SCCs = Tarjan.SCC(ba.T, ba.Q);
        //for(LinkedHashSet<BAState> s : SCCs){
        //  System.out.println(s);
        //  System.out.println("isolated? " + Tarjan.isIsolated(ba.T, s));
        //  System.out.println("total? " + Tarjan.isTotal(ba.T, s, ba.sigma));
        //}
        
        I = new LinkedHashSet();
        LinkedHashSet<BAState> badStates = initialBad(SCCs, ba); 
        //System.out.println("bad: " + badStates);
        fixBad(badStates, ba);
        BA noBadStateBA = removeBad(badStates, ba);     
        //BA noBadStateBA = ba;     
        
        LinkedHashSet<BAState> neverViolate = initialNeverViolate(SCCs, ba);
        //System.out.println("!!! " + neverViolate);
        fixNeverViolate(neverViolate, ba);
        //System.out.println("never violate: " + neverViolate);
        BA oneNoViolateBA = mergeNeverViolate(neverViolate, ba);     
        //BA noBadStateBA = ba;     
        
        BA complete = removeUnreachable(oneNoViolateBA);    
        
        Numbering<BAState> stateNumber = new Numbering();
        I = stateNumber.map(complete.I);
        rename(stateNumber, complete); 
        //System.out.println("*** " + stateNumber.map(badStates));
        //System.out.println("*** " + stateNumber.map(neverViolate));
    }
    
    private LinkedHashSet<BAState> initialBad(LinkedHashSet<LinkedHashSet<BAState>> SCCs, BA ba) {
        LinkedHashSet<BAState> badStates = new LinkedHashSet();
        //page 8 - Fig. 3 - damorim-rosu-2005-cav
        SCCs: 
        for(LinkedHashSet<BAState> scc : SCCs){
            if(Tarjan.isIsolated(ba.T, scc)){
                for(BAState s : scc){
                    if(ba.R.contains(s)) continue SCCs;  
                }
                for(BAState s : scc){
                    badStates.add(s);
                }
            } 
        }
        return badStates;
    }
    
    private void fixBadAux(BAState state, LinkedHashSet<BAState> seen, 
                           LinkedHashSet<BAState> badStates, BA ba){
        seen.add(state);
        if(!ba.T.containsKey(state)) return;
        //go to last children first (DFS)
        for(Set<BAState> destinations : ba.T.get(state).values()) {
            for(BAState destination : destinations){
                if(seen.contains(destination)) continue; 
                fixBadAux(destination, seen, badStates, ba);  
            }
        }
        //now perform bad check, if all possible destinations are bad, the current
        //state is bad
        boolean allLeadToBad = true;
        AllBad:
        for(Set<BAState> destinations : ba.T.get(state).values()) {
            for(BAState destination : destinations){
                if(!badStates.contains(destination)){
                    allLeadToBad = false; break AllBad;
                } 
            }
        }
        if(allLeadToBad) badStates.add(state);
    }
    
    private void fixBad(LinkedHashSet<BAState> badStates, BA ba){
        LinkedHashSet<BAState> seen = new LinkedHashSet(badStates); 
        for(BAState initState : ba.I){
            if(!seen.contains(initState)) fixBadAux(initState, seen, badStates, ba);
        }
    }
    
    static private LinkedHashSet<BAState> filter(LinkedHashSet<BAState> badStates, LinkedHashSet<BAState> original){
        LinkedHashSet<BAState> ret = new LinkedHashSet<BAState>();
        for(BAState state : original){
            if(!badStates.contains(state)) {
                ret.add(state);
            }
        }
        return ret;
    }
    
    private BA removeBad(LinkedHashSet<BAState> badStates, BA ba){
        if(badStates.isEmpty()) return ba;
        BA ret = new BA();
        ret.Q = filter(badStates, ba.Q);
        ret.sigma = ba.sigma;
        ret.T = new LinkedHashMap<BAState, BATransition>();
        for(BAState state : ba.T.keySet()){
            if(badStates.contains(state)) continue;
            BATransition retTrans = new BATransition();
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : ba.T.get(state).keySet()){
                for(BAState destination : ba.T.get(state).get(symbols)){
                    if(badStates.contains(destination)) continue;
                    retTrans.put(symbols, destination); 
                }   
            }
            ret.T.put(state, retTrans);
        }
        ret.I = filter(badStates, ba.I); 
        ret.F = filter(badStates, ba.F);
        ret.R = filter(badStates, ba.R);
        return ret;
    }
    
    private BA mergeNeverViolate(LinkedHashSet<BAState> neverViolate, BA ba){
        if(neverViolate.isEmpty()) return ba;
        BA ret = new BA();
        //pick a state at random to collapse the remaining states in neverViolate 
        //to, i.e., we will replace any state in neverViolate with this state
        BAState collapseTo = neverViolate.iterator().next();
        //remove this state from neverViolate so we can use filter to remove the other
        //states from their given sets
        neverViolate.remove(collapseTo);
        ret.Q = filter(neverViolate, ba.Q);
        ret.sigma = ba.sigma;
        ret.T = new LinkedHashMap<BAState, BATransition>();
        for(BAState state : ba.T.keySet()){
            BATransition retTrans = new BATransition();
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : ba.T.get(state).keySet()){
                for(BAState destination : ba.T.get(state).get(symbols)){
                    retTrans.put(symbols, neverViolate.contains(destination)?collapseTo:destination);
                }   
            }
            ret.T.put(neverViolate.contains(state)?collapseTo:state, retTrans);
        }
        ret.I = filter(neverViolate, ba.I);
        for(BAState init : ba.I){
            if(neverViolate.contains(init)){
                ret.I.add(collapseTo);
                continue;
            }
        } 
        ret.F = filter(neverViolate, ba.F);
        for(BAState s : ba.F){
            if(neverViolate.contains(s)){
                ret.F.add(collapseTo);
                continue;
            }
        } 
        ret.R = filter(neverViolate, ba.R);
        for(BAState s : ba.R){
            if(neverViolate.contains(s)){
                ret.R.add(collapseTo);
                continue;
            }
        } 
        return ret;
    }
    
    
    private LinkedHashSet<BAState> initialNeverViolate(LinkedHashSet<LinkedHashSet<BAState>> SCCs, 
                                                        BA ba) {
        LinkedHashSet<BAState> neverViolate = new LinkedHashSet();
        //page 10 - Fig. 5 - damorim-rosu-2005-cav
        for(LinkedHashSet<BAState> scc : SCCs){
            if(Tarjan.isTotal(ba.T, scc, ba.sigma)){
                for(BAState s : scc){
                    neverViolate.add(s);
                }
            } 
        }
        return neverViolate;
    }
    
    private void fixNeverViolateAux(BAState state, LinkedHashSet<BAState> seen, 
                                    LinkedHashSet<BAState> neverViolate, BA ba){
        seen.add(state);
        if(!ba.T.containsKey(state)) return;
        //go to last children first (DFS)
        for(Set<BAState> destinations : ba.T.get(state).values()) {
            for(BAState destination : destinations){
                if(seen.contains(destination)) continue; 
                fixNeverViolateAux(destination, seen, neverViolate, ba);  
            }
        }
        //now perform neverViolate Check.  If ANY destination for all
        //subsets of sigma is in 
        //neverViolate mark the whole thing neverViolate
        boolean anyNeverViolate = false;
        BATransition trans = ba.T.get(state);
        for(LinkedHashSet<Atom> atoms : ba.sigma){
            anyNeverViolate = false;
            for(BAState destination : trans.getSat(atoms)){
                if(neverViolate.contains(destination)){
                    anyNeverViolate = true; break;
                } 
            }
            if(!anyNeverViolate) return;
        }
        if(anyNeverViolate) neverViolate.add(state);
    }
    
    private void fixNeverViolate(LinkedHashSet<BAState> neverViolate, BA ba){
        LinkedHashSet<BAState> seen = new LinkedHashSet(neverViolate); 
        for(BAState initState : ba.I){
            if(!seen.contains(initState)) fixNeverViolateAux(initState, seen, neverViolate, ba);
        }
    }
    
    private void removeUnreachableAux(BAState state, LinkedHashSet<BAState> seen, BA ba){
        if(!ba.T.containsKey(state)) return;
        seen.add(state);
        //go to last children first (DFS)
        for(Set<BAState> destinations : ba.T.get(state).values()) {
            for(BAState destination : destinations){
                if(seen.contains(destination)) continue; 
                removeUnreachableAux(destination, seen, ba);  
            }
        }
    }
    
    //just going to merge this one's finding with its removal.  I know this is inconsistent
    private BA removeUnreachable(BA ba){
        BA ret = new BA();
        LinkedHashSet<BAState> seen = new LinkedHashSet(); 
        for(BAState initState : ba.I){
            if(!seen.contains(initState)) removeUnreachableAux(initState, seen, ba);
        }
        
        LinkedHashSet<BAState> unseen = new LinkedHashSet(); 
        for(BAState state : ba.Q){
            if(!seen.contains(state)) unseen.add(state);
        }
        
        ret.Q = filter(unseen, ba.Q);
        ret.sigma = ba.sigma;
        ret.T = new LinkedHashMap<BAState, BATransition>();
        for(BAState state : ba.T.keySet()){
            if(!seen.contains(state)) continue;
            BATransition retTrans = new BATransition();
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : ba.T.get(state).keySet()){
                for(BAState destination : ba.T.get(state).get(symbols)){
                    if(unseen.contains(destination)) continue;
                    retTrans.put(symbols, destination);
                }   
            }
            ret.T.put(state, retTrans);
        }
        //initial states MUST be seen
        ret.I = ba.I; 
        ret.F = filter(unseen, ba.F);
        ret.R = filter(unseen, ba.R);
        return ret;
    }
    
    private void rename(Numbering<BAState> stateNumber, BA ba){
        //it is possible to have no initial states, if this occurs
        //we need to output an empty NFA
        if(ba.I.isEmpty() || ba.T.isEmpty()){
            transitions.put(0, new NFATransition()); 
            return;
        }
        for(BAState state : ba.T.keySet()){
            NFATransition trans = new NFATransition(); 
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : ba.T.get(state).keySet()){
                LinkedHashSet<Integer> destinations = new LinkedHashSet();
                for(BAState dState : ba.T.get(state).get(symbols)){
                    destinations.add(stateNumber.get(dState));
                }
                trans.put(symbols, destinations);
            }
            transitions.put(stateNumber.get(state), trans);
        } 
    }
    
    public String toString(){
        String ret = "I = ";
        for(Integer state : I){
            ret += "s" + state + " ";
        }
        ret += "\n";
        for(Integer key : transitions.keySet()){
            ret += "\ns" + key + transitions.get(key); 
        }
        return ret;
    }
    
    public NFATransition get(Integer i) {
        return transitions.get(i);
    }
}
