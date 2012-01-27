package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class PatternMatchAutomaton extends LinkedHashMap<SRS, HashMap<Symbol, SRS>> {
  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(SRS srs : keySet()){
      sb.append(srs.toString());
      HashMap<Symbol, SRS> stateTrans = get(srs); 
      if(stateTrans == null) continue;
      sb.append("[\n");
      for(Symbol s : stateTrans.keySet()){
        sb.append("  ");
        sb.append(s.toString());
        sb.append(" ->\n");
        sb.append(stateTrans.get(s).toPaddedString("    ")); 
      } 
      sb.append("]\n");
    }
    return sb.toString();
  }

  
  //construct a Pattern Match Automaton from an initial SRS
  public PatternMatchAutomaton(SRS srs, ArrayList<Symbol> inputs){
    SRS initial = srs.simplify().initial(); 
    HashSet<SRS> workList = new HashSet<SRS>();
    workList.add(initial);
    construct(inputs, workList);
  }

  //helper method for the constructor
  //originally I was going to do this recursively,
  //but I just kept the nonrecursive helper method anyway
  private void construct(ArrayList<Symbol> inputs, HashSet<SRS> workList){
    while(!workList.isEmpty()){
      SRS srs = workList.iterator().next();
      workList.remove(srs);
      HashMap<Symbol, SRS> stateTrans = new HashMap<Symbol, SRS>();
      put(srs, stateTrans);
      for(Symbol s : inputs){
        SRS nextState = srs.advance(s);
        if(nextState.size() == 0) continue;
        stateTrans.put(s, nextState); 
        if(!containsKey(nextState)) workList.add(nextState);
      }
    }
  }

  //remove non-final rules from the automaton
  //Note that this is destructive
  public PatternMatchAutomaton makeFinal(){
    for(SRS state : keySet()){
      state.makeFinal();
    }
    return this;
  }

}
