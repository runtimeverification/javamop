package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SRS extends LinkedHashSet<Rule> {
  private ArrayList<Variable> variables = new ArrayList<Variable>();

  public ArrayList<Variable> getVariables(){
    return variables;
  }

  public SRS(){
    super();
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(r.toString());
      sb.append(",\n");
    }
    return sb.toString();
  }

  public String toPaddedString(String padding){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(padding);
      sb.append(r.toString());
      sb.append(",\n");
    }
    return sb.toString();
  }

  @Override 
  public boolean add(Rule r){
    variables.addAll(r.getVariables());
    return super.add(r);
  }

  public SRS initial(){
    SRS ret = new SRS();
    for(Rule r : this){
      ret.add(r.initial());    
    }
    return ret;
  }

  //Advance an entire SRS by symbol s DETERMINISTICALLY
  public SRS deterministicAdvance(Symbol s){
    SRS ret = new SRS();
    for(Rule r : this){
      Rule result = r.deterministicAdvance(s);
      if(result != null) {
        ret.add(result);
      }
    }
    return ret;
  }

  // Not USED
//  public SRS advance(Symbol s){
//    SRS ret = new SRS();
//    for(Rule r : this){
//      Rule[] results = r.advance(s);
//      if(results == null) continue;
//      for(Rule r2 : results){
//        ret.add(r2);
//      }
//    }
//    return ret;
//  }

  //this is an inplace modification
  //this method removes all rules from an SRS
  //that are not in accept states (i.e. those without cursor before $).
  public SRS makeFinal(){
    SRS ret = new SRS();
    for(Rule r : this){
      if(r.isFinal())  ret.add(r);
      else             ret.add(RulePlaceHolder.get(r));
    } 
    return ret;
  }

}

