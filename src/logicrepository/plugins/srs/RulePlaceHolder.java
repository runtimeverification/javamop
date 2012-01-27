package logicrepository.plugins.srs;

import java.util.HashMap;

public class RulePlaceHolder extends Rule{
  private static HashMap<Rule, RulePlaceHolder> replacementMap;

  private static int nextId = 0;

  private int id;

  @Override
  public String toString(){
    return "r(" + id + ")";
  }

  private RulePlaceHolder(){
    id = nextId++;
  }

  public RulePlaceHolder get(Rule r){
    RulePlaceHolder rph = replacementMap.get(r);
    if(rph == null){
      rph = new RulePlaceHolder();
      replacementMap.put(r, rph);
    }
    return rph;
  }
}
