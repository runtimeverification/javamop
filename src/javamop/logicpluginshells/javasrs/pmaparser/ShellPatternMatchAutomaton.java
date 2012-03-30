/// TODO : consider symbols that don't appear in the SRS?

package javamop.logicpluginshells.javasrs.pmaparser;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;

//This uses a slightly modified Aho-Corasick automaton
public class ShellPatternMatchAutomaton extends LinkedHashMap<State, HashMap<Symbol, ActionState>> {
  private State s0;
  private boolean hasBegin = false;
  private boolean hasEnd = false;
  private Map<Symbol, Integer> symToNum = null;

  public ShellPatternMatchAutomaton(State s0){
    this.s0 = s0;
  }

  public void setBegin(boolean b){
    hasBegin = b;
  }

  public void setEnd(boolean b){
    hasEnd = b;
  }

  public boolean hasBegin(){
    return hasBegin;
  }

  public boolean hasEnd(){
    return hasEnd;
  }

  @Override public String toString(){
    StringBuilder sb = new StringBuilder("\n");
    sb.append(hasBegin);
    sb.append("\n");
    sb.append(hasEnd);
    sb.append("\n");
    for(State state : keySet()){
      sb.append(state);
      sb.append("\n[");
      HashMap<Symbol, ActionState> transition = get(state);
      for(Symbol symbol : transition.keySet()){
        sb.append("  ");
        sb.append(symbol);
        sb.append(" -> ");
        sb.append(transition.get(symbol));
        sb.append("\n");
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  private Map<Symbol, Integer> mkSymToNum(){
    HashMap<Symbol, ActionState> transition = get(s0);
    HashMap<Symbol, Integer> ret = new HashMap<Symbol, Integer>();
    int i = 0;
    for(Symbol key : transition.keySet()){
      ret.put(key, i++); 
    }
    return ret;
  }

  public Map<Symbol, Integer> getSymToNum(){
    if(symToNum == null){
      symToNum = mkSymToNum();
    }
    return symToNum;
  }

  public String toImplString(){
    Map<Symbol, Integer> symToNum = mkSymToNum();
    StringBuilder sb = new StringBuilder();
    //sb.append(symToNum.toString());
    sb.append("\n\n");
    sb.append("static MOPPMATransitionImpl [][] pma = {");
    for(State state : keySet()){
      sb.append("{");
      HashMap<Symbol, ActionState> transition = get(state);
      for(Symbol symbol : transition.keySet()){
        ActionState astate = transition.get(symbol);
        State s = astate.getState();
        sb.append("new MOPPMATransitionImpl(");
        sb.append(astate.getAction());
        sb.append(", new MOPPMAStateImpl(");
        sb.append(s.getNumber());
        if(s.getMatch() != null){
          s.getMatch().getRhs().getImpl(sb, symToNum);
        }
        sb.append(")),\n");
      }
      sb.append("},\n\n");
    }
    sb.append("};\n");
    return sb.toString();
  }
}

class ActionState {
  private int action;
  private State state;

  public int getAction(){
    return action;
  }

  public State getState(){
    return state;
  }

  public ActionState(int action, State state){
    this.action = action;
    this.state = state;
  }

  @Override public String toString(){
    return "[" + action + "] " + state.toString();
  }

  @Override
  public int hashCode(){
    return action ^ state.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof ActionState)) return false;
    ActionState as = (ActionState) o;
    return(as.action == action && state.equals(as.state));
  }
}

class State {
  private static int counter = 0;
  private int number;
  private int depth;
  private Rule matchedRule = null;

  public State(int number, int depth, Rule matchedRule){
    this.matchedRule = matchedRule;
    this.number = number;
    this.depth = depth;
  }

  public int getNumber(){
    return number;
  }

  public int getDepth(){
    return depth;
  }

  public Rule getMatch(){
    return matchedRule;
  }

  public void setMatch(Rule r){
    matchedRule = r;
  }

  //matched rule must always be equal if number is equal
  //ditto with depth
  @Override
  public int hashCode(){
    return number;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof State)) return false;
    State s = (State) o;
    return s.number == number;
  }

  @Override
  public String toString(){
      return "<" + number 
                 + " @ " 
                 + depth 
                 + ((matchedRule == null)?
                     ""
                   : " matches " + matchedRule.toString()
                   ) 
              + ">";
  }

  static String mkSpaces(int len){
    StringBuilder sb = new StringBuilder();
    for(; len > 0; --len){
      sb.append(' ');
    }
    return sb.toString();
  }
}
