package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rule {
  private static int counter = 0;
  private int number;
  private Set<Symbol> terminals = new HashSet<Symbol>();
  
  private Sequence lhs;
  private Sequence rhs; 

  public Sequence getLhs(){
    return lhs;
  }

  public Sequence getRhs(){
    return rhs;
  }

  protected Rule() {}

  public Rule(Sequence lhs, Sequence rhs){
    number = counter++;
    this.lhs = lhs;
    this.rhs = rhs;
    computeTerminals();
  }

  private void computeTerminals(){
    for(Symbol s : lhs){
      terminals.add(s);
    }
    for(Symbol s : rhs){
      terminals.add(s);
    }
  }

  public Set<Symbol> getTerminals(){
    return terminals;
  }

  @Override
  public String toString(){
    return lhs.toString() + " -> " + rhs.toString();
  }

  @Override
  public boolean equals(Object o){
    if(!(o instanceof Rule)) return false;
    Rule r = (Rule) o;
    return (lhs.equals(r.lhs) && rhs.equals(r.rhs));
  }

  @Override
  public int hashCode(){
    return lhs.hashCode() ^ rhs.hashCode();
  }
}
