package logicrepository.plugins.srs;

public class Rule {
  public Sequence lhs;
  public Sequence rhs; 

  public String toString(){
    return lhs.toString() + " -> " + rhs.toString();
  }


  //some simplifications to an SRS that make generating pattern match
  //automata easier.
  //
  //1) variables concatenation is idempotent 
  //   collapse adjacent variables
  //
  //2) ^A can be removed where A is a Variable
  //
  //3) A$ can be removed where A is, again, a variable
  //
  //See Sequence.simplify for implementation
  public void simplify(){
    lhs = lhs.simplify();
    rhs = rhs.simplify();
  }

}
