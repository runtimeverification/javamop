package logicrepository.plugins.srs;

public class Rule {
  public Sequence lhs;
  public Sequence rhs; 

  public String toString(){
    return lhs.toString() + " -> " + rhs.toString();
  }
}
