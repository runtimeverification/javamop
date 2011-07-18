package logicrepository.plugins.ltl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;

public class NFATransition
  extends LinkedHashMap<LinkedHashSet<LinkedHashSet<Atom>>, LinkedHashSet<Integer>> {
  
  public void put(LinkedHashSet<LinkedHashSet<Atom>> symbols, Integer i){
    if(containsKey(symbols)){
      get(symbols).add(i);
    }
    else{
      LinkedHashSet<Integer> set = new LinkedHashSet();
      set.add(i);
      put(symbols, set);
    }
  }

  //This method gets any transition that has symbols which satisfy that the
  //presented atom set is true,  e.g. if the atom set is [a,b], 
  //[[], [a], [b], [a,b]] satisfies that [a,b] is true
  public TreeSet<Integer> getSat(LinkedHashSet<Atom> atoms){
    TreeSet<Integer> ret = new TreeSet();
    for(LinkedHashSet<LinkedHashSet<Atom>> symbols : keySet()){
      if(symbols.contains(atoms)){
        ret.addAll(get(symbols));
      }
    }
    return ret;
  }

  //This version of getSat is for use with no simultaneous atoms, so it
  //accepts an atom instead of a set of atoms
  //the null Atom will be shorthand for the empty set of Atoms
  public TreeSet<Integer> getSat(Atom atom){
    LinkedHashSet<Atom> singleton = new LinkedHashSet();
    if(atom != null) singleton.add(atom);
    return getSat(singleton);
  }

  public String toString(){
    String ret = "[";
    for(LinkedHashSet<LinkedHashSet<Atom>> symbols : keySet()){
      ret += "\n" + symbols + " -> [";
      
      for(Integer state : get(symbols)){
        ret += "s" + state + ", "; 
      }
      ret = ret.substring(0,ret.length() - 2);
      ret += "]";
    }
    return ret + "\n]\n";
  }
}
