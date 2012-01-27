package logicrepository.plugins.srs;

import java.util.HashMap;

public class RulePlaceHolder extends Rule{
  private static HashMap<Rule, RulePlaceHolder> replacementMap 
    = new HashMap<Rule, RulePlaceHolder>();


  private static int nextId = 0;

  private int id;

  @Override
  public boolean equals(Object o){
    if(!(o instanceof RulePlaceHolder)) return false;
    RulePlaceHolder r = (RulePlaceHolder) o;
    return r.id == id;
  }

  @Override
  public int hashCode(){
    return 1 << (id & 30);
  }

  @Override
  public String toString(){
    return "r(" + id + ")";
  }

  private RulePlaceHolder(){
    id = nextId++;
  }

  public static RulePlaceHolder get(Rule r){
    RulePlaceHolder rph = replacementMap.get(r);
    if(rph == null){
      rph = new RulePlaceHolder();
      replacementMap.put(r, rph);
    }
    return rph;
  }
}
