package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.HashMap;

//class representing a symbol in an ERE
public class Kleene extends ERE {
//  public String name;

  public static ERE get(ERE child){
    return new Kleene(child);
  }

  private Kleene(ERE child){
    children = new ArrayList<ERE>();
	 children.add(child);
  }

  public EREType getEREType(){ 
	  return EREType.STAR;
  }

  public String toString(){
	 return children.get(0) + "*";
  }

  public ERE copy(){
	 return new Kleene(children.get(0).copy());
  }

  public boolean containsEpsilon(){
	 return true;
  }

  public ERE derive(Symbol s){
    return Concat.get(children.get(0).derive(s), copy());
  }
}
