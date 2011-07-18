package logicrepository.plugins.ltl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class DFATransition
  extends LinkedHashMap<Atom, DFAState> {

  public String toString(){
    String ret = "[\n";
      for(Atom a : keySet()){
        //the null Atom corresponds to the default transition
        //which is necessary because we allow users to specify
        //events (atoms) which are not used in the property formula
        if(a == null) ret += "default " + get(a);
        else ret += a + " -> " + get(a) + "\n";
      }    
    return ret + "]\n";
  }
}
