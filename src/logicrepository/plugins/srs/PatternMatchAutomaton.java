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
      sb.append("[\n");
      HashMap<Symbol, SRS> stateTrans = get(srs); 
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

  public PatternMatchAutomaton(SRS srs, ArrayList<Symbol> inputs){
    SRS initial = srs.simplify().initial(); 
    HashSet<SRS> workList = new HashSet<SRS>();
    workList.add(initial);
    construct(inputs, workList);
  }

  private void construct(ArrayList<Symbol> inputs, HashSet<SRS> workList){
    while(!workList.isEmpty()){
      SRS srs = workList.iterator().next();
      workList.remove(srs);
      HashMap<Symbol, SRS> stateTrans = new HashMap<Symbol, SRS>();
      put(srs, stateTrans);
      for(Symbol s : inputs){
        SRS nextState = srs.advance(s);
        stateTrans.put(s, nextState); 
        if(!containsKey(nextState)) workList.add(nextState);
      }
    }
  }
}
