package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.HashMap;

//class representing a symbol in an ERE
public class Negation extends ERE {
//  public String name;

  public static ERE get(ERE child){
	 if(child.getEREType() == EREType.NEG) return child.children.get(0);
    return new Negation(child);
  }

  private Negation(ERE child){
    children = new ArrayList<ERE>();
	 children.add(child);
  }

  public EREType getEREType(){ 
	  return EREType.NEG;
  }

  public String toString(){
	 return "~(" + children.get(0) + ")";
  }

  public ERE copy(){
	 return new Negation(children.get(0).copy());
  }

  public boolean containsEpsilon(){
	 return !(children.get(0).containsEpsilon());
  }

  public ERE derive(Symbol s){
    return Negation.get(children.get(0).derive(s));
  }
}
