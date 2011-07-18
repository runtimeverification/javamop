package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.ArrayList;

//class representing an IFF node in an LTL formula
public class IFF extends LTLFormula {

  IFF(LTLFormula leftChild, LTLFormula rightChild){
    children = new ArrayList<LTLFormula>(2);
    children.add(leftChild);
    children.add(rightChild);
  }

  public LTLType getLTLType(){ 
     return LTLType.IFF;
  }

  protected LTLFormula normalize(boolean b) {
    assert false : "normalize called before lowering!";
    return null;
  }

  protected LTLFormula lower(){
    for(int i = 0; i < children.size(); ++i){
      children.set(i,children.get(i).lower());
    }
    ArrayList<LTLFormula> leftChildren = new ArrayList<LTLFormula>(2);
    ArrayList<LTLFormula> rightChildren = new ArrayList<LTLFormula>(2);
    ArrayList<LTLFormula> nextChildren = new ArrayList<LTLFormula>(2);
    leftChildren.add(new Negation(children.get(0).copy()));
    leftChildren.add(new Negation(children.get(1).copy()));
    rightChildren.add(children.get(0));
    rightChildren.add(children.get(1));
    nextChildren.add(new And(leftChildren));
    nextChildren.add(new And(rightChildren));
    return new Or(nextChildren); 
  }

  public LTLFormula copy(){
    return new IFF(children.get(0).copy(), children.get(1).copy());
  }
}
