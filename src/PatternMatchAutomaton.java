package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PatternMatchAutomaton extends HashMap<SRS, HashMap<Symbol, SRS>> {
  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(SRS srs : keySet()){
      sb.append(srs.toString());
      sb.append("[\n");
      HashMap<Symbol, SRS> stateTrans = get(srs); 
      for(Symbol s : stateTrans.keySet()){
        sb.append("  ");
        sb.append(s.toString());
        sb.append(" ->\n");
        sb.append(stateTrans.get(s).toPaddedString("    ")); 
      } 
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
    while(!workList.empty()){
      SRS srs = workList.iterator().next();
      workList.remove(srs);
      HashMap<Symbol, SRS> stateTrans = new HashMap<Symbol, SRS>();
      put(srs, stateTrans);
      for(Symbol s : inputs){
        SRS nextState = srs.advance(s);
        stateTrans.put(s, nextState); 
        if(!hasKey(nextState)) workList.add(nextState);
      }
    }
  }

  //remove non-final rules from the automaton
  //Note that this is destructive
  public PatternMatchAutomaton finalize(){
    for(SRS state : keySet()){
      state.finalize();
    }
    return this;
  }
}
