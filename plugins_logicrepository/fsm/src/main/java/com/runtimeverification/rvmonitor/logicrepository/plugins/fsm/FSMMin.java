package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Symbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast.Transition;

/**
 * Minimizes the Finite State Machine it is constructed with and provides access 
 * to the minimized properties.
 */
public class FSMMin {
    
    private State startState;
    private ArrayList<Symbol> events;
    private ArrayList<State> states;
    private ArrayList<State> categories;
    private HashMap<State, HashSet<State>> aliases;
    private HashMap<State, Transition> stateMap;
    private static State fail;
    
    static {
        fail = State.get("fail");
    }
    
    /**
     * Construct and minimize the Finite State Machine representation.
     * 
     * @param startState The Finite State Machine initialization state.
     * @param events All the symbols in the Finite State Machine alphabet.
     * @param states All the states in the Finite State Machine.
     * @param categories The Finite State Machine states of user interest.
     * @param aliases A mapping between equivalent states.
     * @param stateMap A mapping from states to their transition mapping.
     */
    public FSMMin(State startState,
           ArrayList<Symbol> events,
           ArrayList<State> states,
           ArrayList<State> categories,
           HashMap<State, HashSet<State>> aliases,
           HashMap<State, Transition> stateMap)
    {
        this.startState = startState;
        this.events = new ArrayList<Symbol>(events);
        this.states = states;
        this.categories = categories;
        this.aliases = aliases; 
        this.stateMap = stateMap;
        
        //this is so we consider the default transitions
        this.events.add(null);
        
        minimize();
    }
    
    /**
     * The state that the Finite State Machine starts on when accepting input.
     * @return The Finite State Machine start state.
     */
    public State getStartState() {
        return startState;
    }
    
    /**
     * All the states in the Finite State Machine.
     * @return A list of all the states.
     */
    public ArrayList<State> getStates() {
        return states;
    }
    
    /**
     * The mapping from states to equivalent states.
     * @return A state to equivalent state mapping.
     */
    public HashMap<State, HashSet<State>> getAliases() {
        return aliases;
    }
    
    /**
     * The mapping from states to their transition mapping.
     * @return A state to state mapping.
     */
    public HashMap<State, Transition> getStateMap() {
        return stateMap;
    }
    
    /**
     * Rewrite the internal state to be a minimized Finite State Machine.
     */
    private void minimize() {
        HashSet<HashSet<State>> L = new HashSet();
        HashSet<HashSet<State>> P = new HashSet();
        initialPartition(L, P);
        while(!L.isEmpty()) {
            Iterator<HashSet<State>> I = L.iterator();
            HashSet<State> S = I.next(); L.remove(S);
            for(Symbol a : events) {
                HashSet<HashSet<State>> nextP = new HashSet();
                for(HashSet<State> B : P) {
                    BlockPair bp = split(B, S, a);
                    
                    boolean block1NotEmpty = !bp.block1.isEmpty();
                    boolean block2NotEmpty = !bp.block2.isEmpty();
                    
                    if(block1NotEmpty) { 
                        nextP.add(bp.block1);
                    }
                    if(block2NotEmpty) {
                        nextP.add(bp.block2);
                    }
                    if(block1NotEmpty && block2NotEmpty) {
                        HashSet<State> target  
                        = (bp.block1.size() < bp.block2.size()) ? bp.block1 : bp.block2;
                        L.add(target);
                    }
                }
                P = nextP;
            }
        }
        
        HashMap<State, State> theta = computeTheta(P);
        
        startState = theta.get(startState);
        rewriteStates(theta);
        rewriteAliases(theta);
        rewriteStateMap(theta);
    }
    
    /**
     * Partition all the states based on categories of interest.
     * @param L Output parameter. Contains the partition.
     * @param P Output parameter. Contains the partition.
     */
    private void initialPartition(HashSet<HashSet<State>> L, HashSet<HashSet<State>> P) {
        //calculate the inverse of aliases, but only for those categories of interest
        HashMap<State, HashSet<State>> ialiases = new HashMap();
        for(State category : categories) {
            if(category == fail) {
                continue;
            }
            //a category may contain only one state
            //not mapped in an alias
            if(!aliases.containsKey(category)) {
                if(!ialiases.containsKey(category)) {
                    ialiases.put(category, new HashSet<State>());
                }
                ialiases.get(category).add(category);
                continue;
            }
            for(State state : aliases.get(category)) {
                if(!ialiases.containsKey(state)) {
                    ialiases.put(state, new HashSet<State>());
                }
                ialiases.get(state).add(category); 
            }
        }
        
        //use the inverse of aliases to create a mapping from sets of categories to sets of 
        //states this allows us to effectively and efficiently merge states that have the 
        //same set of categories
        HashMap<HashSet<State>, HashSet<State>> partitionMap = new HashMap(); 
        for(State state : ialiases.keySet()) {
            HashSet<State> newKey = ialiases.get(state);
            if(!partitionMap.containsKey(newKey)) {
                partitionMap.put(newKey, new HashSet<State>());
            }
            partitionMap.get(newKey).add(state);
        }
        
        //now that the proper states are merged we no longer care about the category keys
        for(HashSet<State> eSet : partitionMap.values()) {
            P.add(eSet);  
            L.add(eSet);
        }
        //add all the states not in a category of interest to their own partition 
        //equivalence set   
        HashSet<State> rest = new HashSet(); 
        for(State state : states) {
            if(!ialiases.containsKey(state)) rest.add(state);
        }
        
        if(!rest.isEmpty()) {
            L.add(rest);
            P.add(rest);
        }
    }
    
    /**
     * Split a set of states based on if they can transition to any of the Goal
     * states on the given symbol. 
     * @param B The set of states to split.
     * @param Goal The set of states to distinguish the division.
     * @param a The symbol to use in the state transitions.
     * @return A pair divided by transitions on the given symbol.
     */
    private BlockPair split(HashSet<State> B, HashSet<State> Goal, Symbol a) {
        HashSet<State> block1 = new HashSet();
        HashSet<State> block2 = new HashSet();
        for(State s : B) {
            Transition t = stateMap.get(s);
            if(t == null) {
                continue;
            }
            if(t.containsSymbol(a)) {
                if(Goal.contains(t.get(a))) {
                    block1.add(s);
                } else {
                    block2.add(s);
                }
            }
            // there is no transition for this symbol so choose block2
            else {
                block2.add(s);
            }
        }
        return new BlockPair(block1, block2); 
    }
    
    /**
     * Compute the mapping from states to the new states that replace them.
     * @param P A HashSet of equivalence class HashSets.
     * @return The mapping from old states to new states.
     */
    private HashMap<State, State> computeTheta(HashSet<HashSet<State>> P) {
        //theta is the common name for a substitution
        HashMap<State, State> theta = new HashMap();   
        //eSet ::= equivalence set
        for(HashSet<State> eSet : P) {
            if(eSet.size() == 1) {
                State old = eSet.iterator().next();
                theta.put(old,old);
                continue;
            }
            String newStateName = "";
            for(State s : eSet) {
                newStateName += "_" + s;
            }
            State newState = State.get(newStateName.substring(1));
            for(State s : eSet) {
                theta.put(s, newState);
            }
        }
        return theta;
    }
    
    /**
     * Rewrite all the stored states with respect to modified states.
     * Replaces the state list with all the value states from the mapping from
     * original states to replacement states.
     * @param theta A mapping from original states to the states replacing them.
     */
    private void rewriteStates(HashMap<State, State> theta) {
        states.clear();
        HashSet<State> collapse = new HashSet();
        for(State state : theta.values()) {
            collapse.add(state);
        }
        for(State state : collapse) {
            states.add(state);
        }
    }
    
    /**
     * Rewrite state aliases with respect to modified states.
     * @param theta A mapping from original states to the states replacing them.
     */
    private void rewriteAliases(HashMap<State, State> theta) {
        HashMap<State, HashSet<State>> newAliases = new HashMap(); 
        for(State alias : aliases.keySet()) {
            HashSet<State> oldStates = aliases.get(alias);
            HashSet<State> newStates = new HashSet();
            for(State state : oldStates) {
                newStates.add(theta.get(state));
            }
            newAliases.put(alias, newStates);
        }
        aliases = newAliases;
    }
    
    /**
     * Rewrite all state transition mappings with respect to modified states.
     * @param theta A mapping from original states to the states replacing them.
     */
    private void rewriteStateMap(HashMap<State, State> theta) {
        HashMap<State, Transition> minStateMap = new HashMap();
        for(State state : stateMap.keySet()) {
            minStateMap.put(theta.get(state), 
                rewriteTransition(stateMap.get(state), theta));
        }
        stateMap = minStateMap;
    }
    
    /**
     * Rebuild a particular transition mapping with respect to modified states.
     * @param theta A mapping from original states to the states replacing them.
     * @return A modified transition that matches the new states.
     */
    private Transition rewriteTransition(Transition t, HashMap<State, State> theta) {
        Transition ret = new Transition();
        for(Symbol event : t.keySet()) {
            ret.put(event, theta.get(t.get(event)));
        }
        return ret;
    }
    
    /**
     * A formatted string of the Finite State Machine.
     * @return The Finite State Machine as a string.
     */
    public String FSMString() {
        String output = startState.toString();
        output += stringOfTransitions(startState);
        for(State key : stateMap.keySet()) {
            if(key == startState) {
                continue;
            }
            output += key;
            output += stringOfTransitions(key);
        }
        for(State key : aliases.keySet()) {
            output += stringOfAlias(key);
        }
        return output;
    }
    
    /**
     * The formatted string of all the transitions from a state.
     * @param state The state to find the transitions for.
     * @return The formatted string for the state transitions.
     */
    private String stringOfTransitions(State state) {
        return "[\n" + stateMap.get(state) + "\n]\n";
    } 
    
    /**
     * The formatted string for a state alias.
     * @param state The state to find the alias for.
     * @return A formatted string with the state and its alias.
     */
    private String stringOfAlias(State alias) {
        String aliasStr = aliases.get(alias).toString();
        return "alias " + alias + " = " + aliasStr.substring(1, aliasStr.length() - 1) + "\n"; 
    }
}

/**
 * A pair of two blocks of states.
 */
class BlockPair {
    public HashSet<State> block1;
    public HashSet<State> block2;
    
    /**
     * Construct the pair of state blocks.
     * @param block1 The first block of states.
     * @param block2 The second block of states.
     */
    BlockPair(HashSet<State> block1, HashSet<State> block2) {
        this.block1 = block1;
        this.block2 = block2;
    }
    
    /**
     * Convert the pair of state blocks into a human-readable string.
     */
    @Override
    public String toString() {
        return "(" + block1 + ", " + block2 + ")";
    }
}
