package logicrepository.plugins.srs;

import java.util.ArrayList;

public class Sequence extends ArrayList<Symbol> {
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Symbol s : this){
      sb.append(s.toString());
      sb.append(" ");
    }
    return sb.toString();
  }

  public Sequence getTerminals(){
    Sequence ret = new Sequence();
    for(Symbol s : this){
      if(s instanceof Terminal){
        ret.add(s);
      }
    }
    return ret;
  }

  public Sequence getVariables(){
    Sequence ret = new Sequence();
    for(Symbol s : this){
      if(s instanceof Variable){
        ret.add(s);
      }
    }
    return ret;
  }
}
