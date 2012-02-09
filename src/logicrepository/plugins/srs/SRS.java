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
  //population the list of actions by determining which
  //Variables need the current symbol pushed
  public ActionSRS deterministicAdvance(Symbol s){
    Symbol cursor = Cursor.get();
    SRS srs = new SRS();
    ArrayList<Variable> actions = new ArrayList<Variable>();
    for(Rule r : this){
      Rule result = r.deterministicAdvance(s);
      if(result != null) {
        srs.add(result);
        //if r == result, then we need to push the current
        //symbol to the Variable following the cursor.
        if(r.equals(result)) {
          boolean cursorFound = false;
          for(Symbol symbol : r.getLhs()){
            if(cursorFound){
              if(symbol instanceof Variable){
                actions.add((Variable) symbol);
                break;
              }
              else {
                throw new RuntimeException("Bug in deterministicAdvance. " 
                    + symbol + "\n" + r + "\n" + result );
              }
            }
            if(symbol == cursor) cursorFound = true;
          }
        }
      }
    }
    return new ActionSRS(actions, srs);
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

