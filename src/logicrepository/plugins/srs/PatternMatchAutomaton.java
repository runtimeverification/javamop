package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class PatternMatchAutomaton extends LinkedHashMap<SRS, HashMap<Symbol, ActionSRS>> {
  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(SRS srs : keySet()){
      sb.append(srs.toString());
      HashMap<Symbol, ActionSRS> stateTrans = get(srs); 
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

  public PatternMatchAutomaton(){
    super();
  }

  //construct a Pattern Match Automaton from an initial SRS
  public PatternMatchAutomaton(SRS srs, ArrayList<Symbol> inputs){
    SRS initial = srs.initial(); 
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
      HashMap<Symbol, ActionSRS> stateTrans = new HashMap<Symbol, ActionSRS>();
      put(srs, stateTrans);
      for(Symbol s : inputs){
        ActionSRS nextState = srs.deterministicAdvance(s);
        if(nextState.srs.size() == 0) continue;
        stateTrans.put(s, nextState); 
        if(!containsKey(nextState.srs)) workList.add(nextState.srs);
      }
    }
  }

  //remove non-final rules from the automaton
  public PatternMatchAutomaton makeFinal(){
    PatternMatchAutomaton ret = new PatternMatchAutomaton();
    for(SRS state : keySet()){
      HashMap<Symbol, ActionSRS> trans = get(state);
      HashMap<Symbol, ActionSRS> finalizedTrans = new HashMap<Symbol, ActionSRS>(); 
      SRS finalizedState = state.makeFinal();
      for(Symbol s : trans.keySet()){
        finalizedTrans.put(s, trans.get(s).makeFinal());
      }
      ret.put(finalizedState, finalizedTrans);
    }
    return ret;
  }

}
