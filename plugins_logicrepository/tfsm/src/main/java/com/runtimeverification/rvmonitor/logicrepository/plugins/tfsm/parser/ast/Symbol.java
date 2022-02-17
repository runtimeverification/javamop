package com.runtimeverification.rvmonitor.logicrepository.plugins.tfsm.parser.ast;

import java.util.HashMap;

public class Symbol {
  private static HashMap<String, Symbol> stringRef;
  private static HashMap<Symbol, String> refString;
  
  static {
    stringRef = new HashMap<String, Symbol>();
	 refString = new HashMap<Symbol, String>();
  }

  public static Symbol get(String s){
    if(stringRef.containsKey(s)){
      return stringRef.get(s);
	 }
	 else {
		Symbol ret = new Symbol();
      stringRef.put(s, ret);
		refString.put(ret, s);
		return ret;
	 }
  }

  public String toString(){
    return refString.get(this);
  }
}
