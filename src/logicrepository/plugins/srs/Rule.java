package logicrepository.plugins.srs;

public class Rule {
  public Sequence lhs;
  public Sequence rhs; 

  public String toString(){
    return lhs.toString() + " -> " + rhs.toString();
  }

  // Simplifying a rule entails first simplifying the sequence on
  // both sides, all this does right now is remove adjacent Variables
  // in favor of one Variable, which makes the pattern match automaton
  // generation faster and less redundant.
  //
  // After the left and right hand side are simplified, we make all rules
  // total.  This means that they reference the beginning and end of the 
  // string (^ and $).  If the rule is already total, nothing need be done.
  // Otherwise we add "^ X*1"  and "X*2 $" to lhs of the rule and X1 and X2 to the
  // beginning and end of the rhs of the rule, respectively.  If the left hand
  // side already begins/ends with variables we simply add "^"/"$" respectively.
  //
  // Note that X*1 and X*2 will never clash because they are not valid Variables
  // from the parser
  public void simplify(){
   // lhs = lhs.simplify();
   // rhs = rhs.simplify();

    Symbol begin = Begin.get();
    Symbol end   = End.get();
    Symbol X1    = Variable.get("X*1");
    Symbol X2    = Variable.get("X*2");

    if(lhs.get(0) != begin){
      if(lhs.get(0) instanceof Variable){
        Sequence prependBeginTo = new Sequence(lhs.size() + 1);
        prependBeginTo.add(begin);
        for(Symbol s : lhs){
          prependBeginTo.add(s);
        }
        lhs = prependBeginTo;
      }
      else {
        Sequence prependBeginVarTo = new Sequence(lhs.size() + 2);
        prependBeginVarTo.add(begin);
        prependBeginVarTo.add(X1);
        for(Symbol s : lhs){
          prependBeginVarTo.add(s);
        }
        lhs = prependBeginVarTo;
        Sequence prependVarTo = new Sequence(rhs.size() + 1);
        prependVarTo.add(X1);
        for(Symbol s : rhs){
          prependVarTo.add(s);
        }
        rhs = prependVarTo;
      }
    }

    if(lhs.get(lhs.size() - 1) != end){
      if(lhs.get(lhs.size() - 1) instanceof Variable){
        lhs.add(end);      
      }
      else {
        lhs.add(X2);
        lhs.add(end);
        rhs.add(X2);
      }
    }
  }
}
