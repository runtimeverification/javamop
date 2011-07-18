package logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

// represents the END state in an Alternating Automaton, should
// never appear in an actual formula
public class END extends LTLFormula {
  
  static public END get(){
    return LTLFormula.theEND;
  }

  public LTLType getLTLType(){ 
     return LTLType.END;
  }

  protected LTLFormula lower(){
     assert false : "cannot lower END!";
     return null;
  }

  protected LTLFormula normalize(boolean b) {
     assert false : "cannot normalize END!";
     return null;
  }

  protected LTLFormula reduce(){
       assert false : "cannot reduce END!";
     return null;
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
    return "END";
  }

  public void subFormulae(LinkedHashSet acc){
    assert false : "END cannot be or have sub formulae!";
  }
}
