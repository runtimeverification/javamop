package logicrepository.plugins.srs;

import java.util.ArrayList;

public class SRS extends ArrayList<Rule> {
  public SRS(){
    super();
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(r.toString());
      sb.append(",\n");
    }
    return sb.toString();
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
  //See Rule.simplify and Sequence.simplify for implementation
  public SRS simplify(){
    for(Rule r : this){
      r.simplify();
    }
    return this;
  }
}

