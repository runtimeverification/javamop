package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast;

import java.util.HashMap;

public class State {
  private static HashMap<String, State> stringRef;
  private static HashMap<State, String> refString;
  
  static {
    stringRef = new HashMap<String, State>();
	 refString = new HashMap<State, String>();
  }

  public static State get(String s){
    if(stringRef.containsKey(s)){
      return stringRef.get(s);
	 }
	 else {
		State ret = new State();
      stringRef.put(s, ret);
		refString.put(ret, s);
		return ret;
	 }
  }

  public String toString(){
    return refString.get(this);
  }
}
