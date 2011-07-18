package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashSet;

//class representing a atom node in an LTL formula
public class Atom extends LTLFormula {
//  public String name;

  static public Atom get(String name){
    Atom self = LTLFormula.stringToRef.get(name); 
    if(self != null) return self;
    Atom ret = new Atom();
    stringToRef.put(name, ret);
    refToString.put(ret, name);
    return ret;
  }

  public LTLType getLTLType(){ 
     return LTLType.A;
  }
 
  protected LTLFormula lower(){
     return this;
  }

  protected LTLFormula normalize(boolean b) {
    //this only evaluates to true if
    //this is a direct child of a boolean
    //op that is being DeMorgan'd
    if(b) return new Negation(this);
    return this;
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
    if(L.getLTLType() == LTLType.A) {
      if(this == L) return 0;
      if(this.hashCode() < L.hashCode()) return -1;
      if(this.hashCode() > L.hashCode()) return 1;
    }
    return LTLType.A.compareTo(L.getLTLType());
  }

  public LTLFormula copy(){
    return this;
  }

  public String toString(){
    return LTLFormula.refToString.get(this);
  }

  public void subFormulae(LinkedHashSet acc){
    acc.add(this);
  }

  public ATransition d(HashMap<LTLFormula, ATransition> D){
    LinkedHashSet<ATuple> retTuples 
    = new LinkedHashSet<ATuple>(1);
    LinkedHashSet<LTLFormula> empty  
    = new LinkedHashSet<LTLFormula>(0);
   
    // We want all the letters in sigma which contain this
    // atom, which is half of sigma
    LinkedHashSet<LinkedHashSet<Atom>> atomSigma
    = new LinkedHashSet<LinkedHashSet<Atom>>(sigma.size() >> 1);
    
    for(LinkedHashSet<Atom> letter : sigma){
      if(letter.contains(this)){
        atomSigma.add(letter);
      }
    }

    retTuples.add(new ATuple(empty, atomSigma, empty));
    return new ATransition(retTuples);
  }
}
