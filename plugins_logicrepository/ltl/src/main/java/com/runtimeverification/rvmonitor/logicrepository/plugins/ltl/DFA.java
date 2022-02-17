package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;

//Tree sets will ensure that we generate the same compound states from the same simple states
//because it guarantees ordering of elements

//Here we finally drop conjunction of atoms... there is no way for more than one atom to be true
//at once from here on out.  Should be easy to convert back at a latter date if we 
//decide to support simultaneous events
public class DFA {
    private  LinkedHashMap<DFAState, DFATransition> transitions = 
        new LinkedHashMap<DFAState, DFATransition>();
    public DFAState I;
    private ArrayList<Atom> atoms = new ArrayList();
    private DFAState violation = DFAState.get("violation");
    
    public DFA(NFA nfa){
        TreeSet<Integer> ISet = new TreeSet(nfa.I);
        I = createDFAState(ISet); 
        
        for(LinkedHashSet<Atom> a : nfa.sigma){
            if(a.size() == 1){
                atoms.add(a.iterator().next());
            } 
        } 
        //we add the null Atom that will correspond to the default transition
        atoms.add(null);
        
        gen(nfa, ISet);
        //determinization may introduce states that must always go to violation
        //so we perform an extra cleanup here
        collapseViolations();
        rename();
    }
    
    private void gen(NFA nfa, TreeSet<Integer> nfaStates){
        DFAState s = createDFAState(nfaStates);
        HashMap<DFAState, TreeSet<Integer>> workList = new HashMap();
        DFATransition dtrans = new DFATransition();
        //for each atom in atoms, we need to see what the next compound state will be
        for(Atom a : atoms){ 
            TreeSet<Integer> states = new TreeSet();
            for(Integer nfaState : nfaStates){
                NFATransition trans = nfa.get(nfaState);
                if(trans == null) continue;
                states.addAll(trans.getSat(a));   
            } 
            DFAState dest = createDFAState(states);
            dtrans.put(a, dest);
            transitions.put(s, dtrans);
            workList.put(dest, states);
        }
        //for each destination in the worklist, we need recursively call gen
        //if it is not already in the dfa.  This must be performed last
        //because we don't add the state transition pair to the DFA
        //until we know all destinations.  The alternative to this would
        //be to keep a map of states we have already considered... I think this
        //is slightly more efficient.  Either way we need one data structure
        //to hold the states, and one to hold the nfa states set that generated
        //that state.  Alternatively, we would recompute the DFAState every time...
        //I don't think it's worth comparing the performance.  Pretty sure 
        //recomputing the DFAState would be worse
        for(DFAState dest : workList.keySet()){
            if(transitions.containsKey(dest)) continue;
            if(dest == violation) {
                transitions.put(dest, new DFATransition());
                continue;
            }
            gen(nfa, workList.get(dest));
        }
    }
    
    private DFAState createDFAState(TreeSet<Integer> states){
        if(states.size() == 0) return violation; 
        String name = "s";
        for(Integer i : states){
            name += i + "_";
        }
        return DFAState.get(name);
    }
    
    private void collapseViolations(){
        LinkedHashSet<DFAState> violations = new LinkedHashSet<DFAState>();
        violations.add(violation);
        findViolations(I, new LinkedHashSet<DFAState>(), violations);
        removeViolations(violations); 
    }
    
    private void findViolations(DFAState s, LinkedHashSet<DFAState> seen, LinkedHashSet<DFAState> violations){
        //depth first traversal
        for(DFAState destination : transitions.get(s).values()){
            if(!seen.contains(destination)){
                seen.add(destination);
                findViolations(destination, seen, violations);
            }
        } 
        
        //at this point it is conventient for violation to be in the violations set
        //when we go to remove the violations this will no longer be the case
        boolean allViolations = true;
        for(DFAState destination : transitions.get(s).values()){
            if(!violations.contains(destination)){
                allViolations = false; break;
            }
        }    
        if(allViolations) violations.add(s);
    }
    
    private void removeViolations(LinkedHashSet<DFAState> violations){
        ArrayList<DFAState> toRemove = new ArrayList();
        //remove violation now because we don't want to remove it from the map
        violations.remove(violation);
        for(DFAState s : transitions.keySet()){
            if(violations.contains(s)){
                toRemove.add(s); 
            }
            else{
                DFATransition trans = transitions.get(s);
                for(Atom a : trans.keySet()){
                    if(violations.contains(trans.get(a))){
                        trans.put(a, violation);
                    }
                }
            }
        }
        
        for(DFAState s : toRemove){
            transitions.remove(s);
        }
    }
    
    private void rename(){
        Numbering<DFAState> stateNum = new Numbering();
        I = DFAState.get("s" + stateNum.get(I));
        ArrayList<DFAState> toRemove = new ArrayList(transitions.size());    
        ArrayList<DFAState> states = new ArrayList(transitions.keySet());
        states.remove(violation);
        for(DFAState s : states){
            toRemove.add(s);
            DFAState newS = DFAState.get("s" + stateNum.get(s));
            DFATransition trans = transitions.get(s);
            DFATransition newT = new DFATransition(); 
            for(Atom a : trans.keySet()){
                DFAState dest = trans.get(a);
                if(dest == violation) newT.put(a, violation);
                else newT.put(a, DFAState.get("s" + stateNum.get(dest))); 
            }
            transitions.put(newS, newT);
        }
        
        for(DFAState s : toRemove){
            transitions.remove(s);
        }
    }
    
    public String toString(){
        //put the initial state first
        DFATransition trans = transitions.get(I);
        if(trans == null) return I + "[\n  default violation\n]\n\nviolation[\n]\n";
        String ret = I.toString() + trans;
        
        for(DFAState key : transitions.keySet()){
            //don't repeat the initial state
            if(key == I) continue;
            ret += "\n" + key + transitions.get(key); 
        }
        return ret;
    }
    
}
