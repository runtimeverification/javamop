package logicrepository.plugins.ere;

import java.util.HashMap;

//class representing a symbol in an ERE
public class Empty extends ERE {
//  public String name;

  static public Empty get(){
	 return empty;
  }

  public EREType getEREType(){ 
	  return EREType.EMP;
  }

  public boolean equals(Object o){
	 return this == o;
  }
 
  public int compareTo(Object o){
	 if(!(o instanceof ERE)) return -1;
	 ERE E = (ERE) o;
	 return EREType.EMP.compareTo(E.getEREType());
  }

  public ERE copy(){
    return this;
  }

  public String toString(){
    return "empty";
  }

  public boolean containsEpsilon(){
    return false;
  }

  public ERE derive(Symbol s){
    return empty;
  }
}
