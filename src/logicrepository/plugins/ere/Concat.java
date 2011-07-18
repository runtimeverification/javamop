package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.HashMap;

//class representing a symbol in an ERE
public class Concat extends ERE {
//  public String name;
 
  public static ERE get(ERE left, ERE right){
    Concat cat = new Concat(left, right);
    ERE ret = cat.simplify();
	 return ret;
  }

  private Concat(ERE left, ERE right){
    children = new ArrayList<ERE>(2);
	 children.add(left);
	 children.add(right);
  }

  private ERE simplify(){
    if(children.get(0) == empty) return empty;
	 if(children.get(1) == empty) return empty;
	 if(children.get(0) == epsilon) return children.get(1);
	 if(children.get(1) == epsilon) return children.get(0);
	 return this;
  }

  public EREType getEREType(){ 
	  return EREType.CAT;
  }

  public String toString(){
    return "(" + children.get(0) + " " + children.get(1) + ")";
  }

  public ERE copy(){
	 return new Concat(children.get(0).copy(), children.get(1).copy());
  }

   public boolean containsEpsilon(){
    for(ERE child : children){
		 if(!child.containsEpsilon()) return false;
	 }
	 return true;
  }

  public ERE derive(Symbol s){
    ERE left = children.get(0);
	 ERE right = children.get(1);
    if(left.containsEpsilon()){
      ArrayList<ERE> orChildren = new ArrayList<ERE>(2);
      orChildren.add(Concat.get(left.derive(s), right.copy()));
		orChildren.add(right.derive(s));
		return Or.get(orChildren);
	 }
    return Concat.get(left.derive(s), right.copy());
  }
}
