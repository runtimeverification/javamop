package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.State;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.Symbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast.Transition;

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

  FSMMin(State startState,
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

    minamize();
  }

  public State getStartState(){
    return startState;
  }

  public ArrayList<State> getStates(){
    return states;
  }

  public HashMap<State, HashSet<State>> getAliases(){
    return aliases;
  }

  public HashMap<State, Transition> getStateMap(){
    return stateMap;
  }

  private void minamize(){
    HashSet<HashSet<State>> L = new HashSet();
    HashSet<HashSet<State>> P = new HashSet();
    initialPartition(L, P);
    while(!L.isEmpty()){
      Iterator<HashSet<State>> I = L.iterator();
      HashSet<State> S = I.next(); L.remove(S);
      for(Symbol a : events){
        HashSet<HashSet<State>> nextP = new HashSet();
        for(HashSet<State> B : P){
          BlockPair bp = split(B, S, a);
          
          boolean block1NotEmpty = !bp.block1.isEmpty();
          boolean block2NotEmpty = !bp.block2.isEmpty();

          if(block1NotEmpty) { 
             nextP.add(bp.block1);
          }
          if(block2NotEmpty) {
             nextP.add(bp.block2);
          }
          if(block1NotEmpty && block2NotEmpty){
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

  private void initialPartition(HashSet<HashSet<State>> L, HashSet<HashSet<State>> P){
    //calculate the inverse of aliases, but only for those categories of interest
    HashMap<State, HashSet<State>> ialiases = new HashMap();
    for(State category : categories){
      if(category == fail) continue;
      //a category may contain only one state
      //not mapped in an alias
      if(!aliases.containsKey(category)){
        if(!ialiases.containsKey(category)){
          ialiases.put(category, new HashSet<State>());
        }
        ialiases.get(category).add(category);
        continue;
      }
      for(State state : aliases.get(category)){
        if(!ialiases.containsKey(state)){
          ialiases.put(state, new HashSet<State>());
        }
        ialiases.get(state).add(category); 
      }
    }

   //use the inverse of aliases to create a mapping from sets of categories to sets of 
   //states this allows us to effectively and efficiently merge states that have the 
   //same set of categories
   HashMap<HashSet<State>, HashSet<State>> partitionMap = new HashMap(); 
   for(State state : ialiases.keySet()){
      HashSet<State> newKey = ialiases.get(state);
      if(!partitionMap.containsKey(newKey)){
        partitionMap.put(newKey, new HashSet<State>());
      }
      partitionMap.get(newKey).add(state);
   }

   //now that the proper states are merged we no longer care about the category keys
   for(HashSet<State> eSet : partitionMap.values()){
     P.add(eSet);  
     L.add(eSet);
   }
   //add all the states not in a category of interest to their own partition 
   //equivalence set   
   HashSet<State> rest = new HashSet(); 
   for(State state : states){
     if(!ialiases.containsKey(state)) rest.add(state);
   }

   if(!rest.isEmpty()){
     L.add(rest);
     P.add(rest);
   }
  }

  private BlockPair split(HashSet<State> B, HashSet<State> Goal, Symbol a){
    HashSet<State> block1 = new HashSet();
    HashSet<State> block2 = new HashSet();
    for(State s : B){
      Transition t = stateMap.get(s);
      if(t == null) continue;
      if(t.containsSymbol(a)){
        if(Goal.contains(t.get(a))) block1.add(s);
        else block2.add(s);
      }
      // there is no transition for this symbol so choose block2
      else block2.add(s);
    }
    return new BlockPair(block1, block2); 
  }

  private HashMap<State, State> computeTheta(HashSet<HashSet<State>> P){
    //theta is the common name for a substitution
    HashMap<State, State> theta = new HashMap();   
    //eSet ::= equivalence set
    for(HashSet<State> eSet : P){
      if(eSet.size() == 1) {
        State old = eSet.iterator().next();
        theta.put(old,old);
        continue;
      }
      String newStateName = "";
      for(State s : eSet){
        newStateName += "_" + s;
      }
      State newState = State.get(newStateName.substring(1));
      for(State s : eSet){
        theta.put(s, newState);
      }
    }
    return theta;
  }

  private void rewriteStates(HashMap<State, State> theta){
    states.clear();
    HashSet<State> collapse = new HashSet();
    for(State state : theta.values()){
       collapse.add(state);
    }
    for(State state : collapse){
       states.add(state);
    }
  }

  private void rewriteAliases(HashMap<State, State> theta){
    HashMap<State, HashSet<State>> newAliases = new HashMap(); 
    for(State alias : aliases.keySet()){
      HashSet<State> oldStates = aliases.get(alias);
      HashSet<State> newStates = new HashSet();
      for(State state : oldStates){
        newStates.add(theta.get(state));
      }
      newAliases.put(alias, newStates);
    }
    aliases = newAliases;
  }

  private void rewriteStateMap(HashMap<State, State> theta){
    HashMap<State, Transition> minStateMap = new HashMap();
    for(State state : stateMap.keySet()){
      minStateMap.put(theta.get(state), 
                      rewriteTransition(stateMap.get(state), theta));
    }
    stateMap = minStateMap;
  }

  private Transition rewriteTransition(Transition t, HashMap<State, State> theta){
    Transition ret = new Transition();
    for(Symbol event : t.keySet()){
      ret.put(event, theta.get(t.get(event)));
    }
    return ret;
  }

  public String FSMString(){
    String output = startState.toString();
    output += stringOfTransitions(startState);
    for(State key : stateMap.keySet()){
      if(key == startState) continue;
      output += key;
      output += stringOfTransitions(key);
    }
    for(State key : aliases.keySet()){
      output += stringOfAlias(key);
    }
    return output;
  }

  private String stringOfTransitions(State state){
    return "[\n" + stateMap.get(state) + "\n]\n";
  } 

  private String stringOfAlias(State alias){
    String aliasStr = aliases.get(alias).toString();
    return "alias " + alias + " = " + aliasStr.substring(1, aliasStr.length() - 1) + "\n"; 
  }
}

class BlockPair {
  public HashSet<State> block1;
  public HashSet<State> block2;

  BlockPair(HashSet<State> block1, HashSet<State> block2){
    this.block1 = block1;
    this.block2 = block2;
  }

  public String toString(){
     return "(" + block1 + ", " + block2 + ")";
  }
}
