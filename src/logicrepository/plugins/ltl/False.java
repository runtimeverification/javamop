package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.HashMap;

//class representing a False node in an LTL formula
public class False extends LTLFormula {

  static public False get(){
    return LTLFormula.theFalse;    
  }

  public LTLType getLTLType(){ 
     return LTLType.F;
  }

  protected LTLFormula lower(){
     return this;
  }

  protected LTLFormula normalize(boolean b) {
    if(b) return True.get();
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
    return LTLType.F.compareTo(L.getLTLType());
  }

  public LTLFormula copy(){
   return this;
  }

  public String toString(){
    return "false";
  }

  public void subFormulae(LinkedHashSet acc){
    acc.add(this);
  }

  public ATransition d(HashMap<LTLFormula, ATransition> D){
    LinkedHashSet<ATuple> retTuples 
    = new LinkedHashSet<ATuple>(0);
    return new ATransition(retTuples);
  }
}
