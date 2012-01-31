package logicrepository.plugins.srs;

public class Rule {
  public Sequence lhs;
  public Sequence rhs; 

  @Override
  public String toString(){
    return lhs.toString() + " -> " + rhs.toString();
  }

  @Override
  public boolean equals(Object o){
    if(!(o instanceof Rule)) return false;
    Rule r = (Rule) o;
    return (lhs.equals(r.lhs) && rhs.equals(r.rhs));
  }

  @Override
  public int hashCode(){
    return lhs.hashCode() ^ rhs.hashCode();
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


  //We have switched to deterministic pattern matching since it
  //is actually... feasible.  Keeping the old version of the method
  //for posterity below
  //
  //advance the cursor in a rule
  //by a given symbol
  //return null if it cannot be advanced
  //(such as if the cursor is before Terminal "a"
  //and we are advancing by Terminal "b"
  public Rule deterministicAdvance(Symbol s) {
    Rule ret = new Rule();
    Sequence advancedLhs = lhs.deterministicAdvance(s);
    if(advancedLhs == null) return null;
    ret.lhs = advancedLhs;
    ret.rhs = rhs;
    return ret;
  }

  // NOT USED
  //advance the cursor in a rule
  //by a given symbol
  //return null if it cannot be advanced
  //(such as if the cursor is before Terminal "a"
  //and we are advancing by Terminal "b"
  public Rule[] advance(Symbol s){
    Sequence[] results = lhs.advance(s);
    if(results == null) return null;
    Rule[] ret = new Rule[results.length];
    ret[0] = new Rule();
    ret[0].lhs = results[0];
    ret[0].rhs = rhs;
    if(results.length == 2){
      ret[1] = new Rule();
      ret[1].lhs = results[1];
      ret[1].rhs = rhs;
    }
    return ret;
  }


  //return whether or not a rule is final (has the cursor before $).
  public boolean isFinal(){
    if(lhs.size() < 3) return false;
    Symbol cursor = Cursor.get();
    return (cursor == lhs.get(lhs.size() - 2))
          || ((cursor == lhs.get(lhs.size() - 3) 
                && (lhs.get(lhs.size() - 2) instanceof Variable))) ;
  }
}
