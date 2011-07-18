package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.HashMap;

//class representing a True node in an LTL formula
public class True extends LTLFormula {
  
  static public True get(){
    return LTLFormula.theTrue;
  }

  public LTLType getLTLType(){ 
     return LTLType.T;
  }

  protected LTLFormula lower(){
     return this;
  }

  protected LTLFormula normalize(boolean b) {
    if(b) return False.get();
    else  return this;
  }

  protected LTLFormula reduce(){
     return this;
  }


  public boolean equals(Object o){
    return this == o;
  }

  public int compareTo(Object o){
    if(!(o instanceof LTLFormula)) return -1;
    LTLFormula L = (LTLFormula) o;
    return LTLType.T.compareTo(L.getLTLType());
  }

  public LTLFormula copy() {
    return this;
  }

  public String toString(){
    return "true";
  }

  public void subFormulae(LinkedHashSet acc){
    acc.add(this);
  }

  public ATransition d(HashMap<LTLFormula, ATransition> D){
    LinkedHashSet<ATuple> retTuples 
    = new LinkedHashSet<ATuple>(1);
    LinkedHashSet<LTLFormula> empty  
    = new LinkedHashSet<LTLFormula>(0);
    
    retTuples.add(new ATuple(empty, sigma, empty));
    return new ATransition(retTuples);
  }
}
