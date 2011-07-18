package logicrepository.plugins.ltl;

import java.io.PrintStream;
import java.util.ArrayList;

//class representing an XOr node in an LTL formula
public class XOr extends LTLFormula {

  XOr(ArrayList<LTLFormula> children){
    assert children != null && children.size() >= 2 
        : "XOr requires at least two children!";
    this.children = children;
  }

  public LTLType getLTLType(){ 
     return LTLType.XOR;
  }

  public LTLFormula normalize(){
     return normalize(false);
  }

  protected LTLFormula normalize(boolean b) {
    assert false : "normalize called before lowering!";
    return null;
  }

  protected LTLFormula lower(){
    for(int i = 0; i < children.size(); ++i){
      children.set(i,children.get(i).lower());
    }
    ArrayList<LTLFormula> nextChildren;    
    LTLFormula left = children.get(0);
    for(int i = 1; i < children.size(); ++i){
      nextChildren = new ArrayList<LTLFormula>(2);
      LTLFormula right = children.get(i);
      nextChildren.add(new Negation(left.copy()));
      nextChildren.add(right.copy());
      LTLFormula And1 = new And(nextChildren);
      nextChildren = new ArrayList<LTLFormula>(2);
      nextChildren.add(left);
      nextChildren.add(new Negation(right));
      LTLFormula And2 = new And(nextChildren);
      nextChildren = new ArrayList<LTLFormula>(2);
      nextChildren.add(And1);
      nextChildren.add(And2);
      left = new Or(nextChildren);
    }
    return left;
  }

  public LTLFormula copy() {
    ArrayList<LTLFormula> copiedChildren = new ArrayList<LTLFormula>(children.size());
    for(LTLFormula child : children){
      copiedChildren.add(child.copy());
    }
    return new XOr(copiedChildren);
  }

  public static void main(String[] args){
     ArrayList<LTLFormula> c = new ArrayList<LTLFormula>();
     ArrayList<LTLFormula> q = new ArrayList<LTLFormula>();
     c.add(Atom.get("'a"));
     c.add(Atom.get("'b"));
     c.add(Atom.get("'c"));
    // c.add(Atom.get("'d"));
     q.add(Atom.get("'z"));
     q.add(Atom.get("'q"));
     c.add(new Negation(new And(q)));
     LTLFormula f = new XOr(c);
     System.out.println("red");
     System.out.println(f.lower());
     System.out.println(" == ");
     f = f.simplify();
     System.out.println(f);
     System.out.println(".");
  }
}
