package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

//class representing an DualPreviously node in an LTL formula
public class DualPreviously extends LTLFormula {

  DualPreviously(LTLFormula child){
    children = new ArrayList<LTLFormula>(1);
    children.add(child);
  }

  public LTLType getLTLType(){ 
     return LTLType.DY;
  }

  protected LTLFormula normalize(boolean b) {
    if(b) {
      return new Previously(
        new Negation(children.get(0)).normalize(false));
    }
    else{
      children.set(0,children.get(0).normalize(false));
      return this;
    }
  }

  public LTLFormula copy(){
    return new DualPreviously(children.get(0).copy());
  }

 public ATransition d(HashMap<LTLFormula, ATransition> D){
    LinkedHashSet<ATuple> retTuples 
   = new LinkedHashSet<ATuple>(1);
    LinkedHashSet<LTLFormula> empty  
   = new LinkedHashSet<LTLFormula>(0);
    LinkedHashSet<LinkedHashSet<LTLFormula>> previousSet
   = children.get(0).toSetForm();
    LinkedHashSet<LTLFormula> ENDprevious  
   = new LinkedHashSet<LTLFormula>(1);
    ENDprevious.add(END.get());

    for(LinkedHashSet<LTLFormula> previous : previousSet){
      retTuples.add(new ATuple(previous, sigma, empty));
    }
    retTuples.add(new ATuple(ENDprevious, sigma, empty)); 
    return new ATransition(retTuples);
  }
}
