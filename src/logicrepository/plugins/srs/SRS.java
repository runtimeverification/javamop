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
  public void makeFinal(){
    ArrayList<Rule> toReplace = new ArrayList<Rule>();
    for(Rule r : this){
      if(!r.isFinal()) {
        System.out.println("removing ===> " + r);
        remove(r);
        toReplace.add(r);
      }
    } 
    for(Rule r : toReplace){
      add(RulePlaceHolder.get(r));
    } 
  }

}

