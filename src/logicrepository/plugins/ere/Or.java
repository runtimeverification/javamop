package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

//class representing or in an ERE
public class Or extends ERE {
//  public String name;

  static public ERE get(ArrayList<ERE> children){
    Or or = new Or(children);
	 ERE ret = or.simplify();
	 return ret;
  }

  private Or(ArrayList<ERE> children){
    assert children != null && children.size() >= 2 
     : "Or requires at least two children!";
    this.children = children;
  }

  public ERE simplify(){
	 ArrayList<ERE> flattened;
	 ArrayList<ERE> previous = children;
	 boolean changed;
	 do {
	   changed = false; 
		flattened = new ArrayList<ERE>(children.size() >> 1);
      for(ERE child : previous){
        if(child.getEREType() == EREType.OR){
           flattened.addAll(child.getChildren());
			  changed = true;
		  } else {
           flattened.add(child); 
		  }
		}
      previous = flattened;
	 } while(changed);
	 children = flattened;
	 Collections.sort(children);
    for(int i = 0; i < children.size(); ++i){
      if(children.get(i) == empty){
        children.remove(i);
		}
	 }
	 for(int i = 0; i < children.size() - 1; ++i){
      if(children.get(i).equals(children.get(i + 1))){
        children.remove(i);
		}
	 }
	 if(children.size() == 0) return empty;
	 if(children.size() == 1) return children.get(0);
	 return this;
  }

  public EREType getEREType(){ 
	  return EREType.OR;
  }

  public String toString(){
    String ret = "(" + children.get(0);
	 for(int i = 1; i < children.size(); ++i){
      ret += " | " + children.get(i);
	 }
	 ret += ")";
	 return ret;
  }

  public ERE copy(){
    ArrayList<ERE> retChildren = new ArrayList<ERE>(children.size());
	 for(ERE child : children){
      retChildren.add(child.copy());
	 }
	 return new Or(retChildren);
  }

  public boolean containsEpsilon(){
    for(ERE child : children){
		 if(child.containsEpsilon()) return true;
	 }
	 return false;
  }

  public ERE derive(Symbol s){
	 ArrayList<ERE> orChildren = new ArrayList<ERE>(children.size());
    for(ERE child : children){
      orChildren.add(child.derive(s));
	 }
	 return Or.get(orChildren);
  }
}
