package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

//class representing an DualUntil node in an LTL formula
public class DualUntil extends LTLFormula {

  DualUntil(LTLFormula leftChild, LTLFormula rightChild){
    children = new ArrayList<LTLFormula>(2);
    children.add(leftChild);
    children.add(rightChild);
  }

  public LTLType getLTLType(){ 
     return LTLType.DU;
  }

  protected LTLFormula normalize(boolean b) {
    if(b) {
      return new Until(
        new Negation(children.get(0)).normalize(false),
        new Negation(children.get(1)).normalize(false));
    }
    else{
      children.set(0,children.get(0).normalize(false));
      children.set(1,children.get(1).normalize(false));
      return this;
    }
  }

  public LTLFormula copy(){
    return new DualUntil(children.get(0).copy(), children.get(1).copy());
  }

  //This goes against the formulation in the paper
  //we add the Until ndoe and the End to the same
  //tuple...because it is equivalent and uses less memory
  public ATransition d(HashMap<LTLFormula, ATransition> D){
    LinkedHashSet<ATuple> retTuples 
   = new LinkedHashSet<ATuple>();
    LinkedHashSet<LTLFormula> empty  
   = new LinkedHashSet<LTLFormula>(0);
    LinkedHashSet<LTLFormula> next  
   = new LinkedHashSet<LTLFormula>(1);
    LinkedHashSet<LTLFormula> ENDnext  
   = new LinkedHashSet<LTLFormula>(1);

    retTuples.addAll(D.get(children.get(0)).tuples);
    next.add(this);
    ENDnext.add(END.get());
    retTuples.add(new ATuple(empty, sigma, next));
    retTuples.add(new ATuple(empty, sigma, ENDnext));

    return new ATransition(retTuples).and(D.get(children.get(1)));
  }
}
