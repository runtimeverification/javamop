package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SRS extends LinkedHashSet<Rule> {
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

  public SRS simplify(){
    for(Rule r : this){
      r.simplify();
    }
    return this;
  }

  public SRS initial(){
    Symbol cursor = Cursor.get();
    Symbol begin = Begin.get();
    SRS ret = new SRS();
    for(Rule r : this){
      Rule retRule = new Rule();
      retRule.rhs = r.rhs;
      retRule.lhs = new Sequence();
      retRule.lhs.add(begin);
      retRule.lhs.add(cursor);
      for(int i = 1; i < r.lhs.size(); ++i){
        retRule.lhs.add(r.lhs.get(i));
      }
      ret.add(retRule);
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
  public SRS advance(Symbol s){
    SRS ret = new SRS();
    for(Rule r : this){
      Rule[] results = r.advance(s);
      if(results == null) continue;
      for(Rule r2 : results){
        ret.add(r2);
      }
    }
    return ret;
  }

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

