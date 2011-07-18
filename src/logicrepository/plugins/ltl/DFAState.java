package logicrepository.plugins.ltl;

import java.util.HashMap;

public class DFAState{
  static HashMap<String, DFAState> stringRef;
  static HashMap<DFAState, String> refString;

  static {
    stringRef = new HashMap();
    refString = new HashMap();
  }

  private DFAState(){}

  public static DFAState get(String name){
    if(stringRef.containsKey(name)) return stringRef.get(name);
    DFAState s = new DFAState();
    stringRef.put(name, s);
    refString.put(s, name);
    return s;
  }

  public String toString(){
    return refString.get(this);
  }
}
