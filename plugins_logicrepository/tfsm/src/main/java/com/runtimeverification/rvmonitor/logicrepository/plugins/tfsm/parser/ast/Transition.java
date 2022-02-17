package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast;

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;

//Essentially this class is just a wrapper for a HashMap
//in order to provide a different toString method
public class Transition{
  private HashMap<Symbol, State> contents; 

  public Transition(){
    contents = new HashMap<Symbol, State>();
  }

  public void put(Symbol e, State s){
	  contents.put(e,s);
  }

  public boolean containsSymbol(Symbol e){
	  return contents.containsKey(e);
  }

  public State get(Symbol e){
    return contents.get(e);
  }

  public Set<Symbol> keySet(){
    return contents.keySet();
  }

  public Collection<State> values(){
    return contents.values();
  }

  public int size(){
	  return contents.size();
  }

  public boolean isEmpty(){
	  return contents.size() == 0;
  }

  public String toString(){
	 if(contents.keySet().size() == 0) return "";
    String ret = "";
	 for(Symbol event : contents.keySet()){
     if(event == null) ret += "  default " + contents.get(null) + "\n";
     else ret += "  " + event + " -> " + contents.get(event) + "\n";
    }
	 return ret.substring(0, ret.length() - 1);
  }
}
