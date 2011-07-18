package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.HashMap;

//abstract class for the internal representation of our 
//ERE formulas
public abstract class ERE implements Comparable{
  protected ArrayList<ERE> children;  
  protected int hash = 0;

  protected static HashMap<String, Symbol> stringToRef;
  protected static HashMap<Symbol, String> refToString;
  protected static Epsilon epsilon;
  protected static Empty empty;
  static {
    stringToRef = new HashMap<String, Symbol>();
	 refToString = new HashMap<Symbol, String>();
	 epsilon = new Epsilon();
	 empty = new Empty();
  }

  public abstract EREType getEREType();
  public ArrayList<ERE> getChildren(){
    return children;
  }
  
  public abstract String toString();

  // SYNTACTIC compareTo of ERE
  // The return values can be thought of
  // as follows (standard Java practice)
  // -1 := <, 0 := ==, 1 := >
  //
  // this is inherited my all subclasses
  // save Symbol, True, and False
  public int compareTo(Object o){
	 // we want to push all non-EREs
	 // to the end, in a sort.  In this project
	 // o should always be an ERE, however
	 if(!(o instanceof ERE)) return -1;
    ERE L = (ERE) o;
	 // If this node and the node being compared
	 // have different types, return the comparison
	 // of those types, we are done
    if(L.getEREType() != getEREType()) {
      return getEREType().compareTo(L.getEREType());
	 }
	 //If, instead, the types are equal the comparison
	 //must be based on the children, from left to right
    ArrayList<ERE> lChildren = L.getChildren();
	 for(int i = 0; i < children.size(); ++i){
		int res = children.get(i).compareTo(lChildren.get(i));
      if(res != 0) return res; 
	 }
	 //If all children are equal these nodes must be equal
	 return 0;
  }

  // SYNTACTIC equality of ERE
  // This could be implemented simply by checking
  // if compareTo is 0, but this method should be
  // slightly faster, and equality is important
  // for sets and maps
  //
  // The idea here is similar to compare to
  // and this method is inherited by all subclasses
  // save Symbol, True, and False
  public boolean equals(Object o){
	 if(!(o instanceof ERE)) return false;
    ERE ere = (ERE) o;
    if(ere.getEREType() != getEREType()) return false; 
    ArrayList<ERE> lChildren = ere.getChildren();
	 for(int i = 0; i < children.size(); ++i){
       if(!children.get(i).equals(lChildren.get(i))){
         return false;
		 }
	 }
	 return true;
  }

  //let's actually store the hashcode so we 
  //don't recursively compute it EVERY time
  public int hashCode(){
	 if(hash != 0) {
		 return hash;
	 }
    if(children == null) {
	   hash = super.hashCode();
	   return hash;
	 }
	 hash = getEREType().toInt();
	 for(ERE child : children){
      hash ^= child.hashCode();
	 }
	 return hash;
  }

  // This returns a copy of a given ERE, I prefer
  // the explicit word copy to a copy constructor, which
  // is often less clear
  public abstract ERE copy();

  public abstract boolean containsEpsilon(); 

  public abstract ERE derive(Symbol s);
}
