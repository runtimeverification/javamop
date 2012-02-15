package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Rule {
  private static int counter = 0;
  private int number;
  
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
