package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Rule {
  private static int counter = 0;
  private int number;
  
  private Sequence lhs;
  private Sequence rhs; 

  private ArrayList<Variable> variables 
    = new ArrayList<Variable>();
  private ArrayList<ArrayList<Variable>> eqVariables 
    = new ArrayList<ArrayList<Variable>>();  

  public Sequence getLhs(){
    return lhs;
  }

  public Sequence getRhs(){
    return rhs;
  }

  public ArrayList<Variable> getVariables(){
    return variables;
  }

  public ArrayList<ArrayList<Variable>> getEqVariables(){
    return eqVariables;
  }  

  protected Rule() {}

  public Rule(Sequence lhs, Sequence rhs){
    number = counter++;
    this.lhs = lhs;
    this.rhs = rhs;
    simplify();
    computeVariables();
  }

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
  private void simplify(){
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
    ret.variables = variables;
    ret.eqVariables = eqVariables;
    return ret;
  }

  // NOT USED
  //advance the cursor in a rule
  //by a given symbol
  //return null if it cannot be advanced
  //(such as if the cursor is before Terminal "a"
  //and we are advancing by Terminal "b"
//  public Rule[] advance(Symbol s){
//    Sequence[] results = lhs.advance(s);
//    if(results == null) return null;
//    Rule[] ret = new Rule[results.length];
//    ret[0] = new Rule();
//    ret[0].lhs = results[0];
//    ret[0].rhs = rhs;
//    if(results.length == 2){
//      ret[1] = new Rule();
//      ret[1].lhs = results[1];
//      ret[1].rhs = rhs;
//    }
//    return ret;
//  }


  private void computeVariables(){
    int uniqueId = 0;
    ArrayList<Variable> lhsVariables = lhs.getVariables();
    ArrayList<Variable> rhsVariables = rhs.getVariables();
    //variables that have already been seen in the lhs of this
    //rule.  Used to find non-linear variable occurences
    HashSet<Variable> seen = new HashSet<Variable>();
    //used to compute the final list of lists of variables that need
    //to be checked for equivalency in the presence of non-linear patterns
    HashMap<Variable, HashSet<Variable>> nonLinearVariableFinderMap
      = new HashMap<Variable, HashSet<Variable>>();
    //map for replacing Variables with new uniquely named Variables
    //in the rhs, but only in the rhs
    HashMap<Variable, Variable> replaceMap = new HashMap<Variable, Variable>();
    for(Variable v : lhsVariables){
      if(seen.contains(v)){
        StringBuilder namePrefix = new StringBuilder();
        namePrefix.append(number);
        namePrefix.append(v.toString());
        String prefixString = namePrefix.toString();
        HashSet<Variable> checkSet = nonLinearVariableFinderMap.get(v);
        if(checkSet == null){
          checkSet = new HashSet<Variable>();
          checkSet.add((Variable) Variable.get(prefixString));
          nonLinearVariableFinderMap.put(v, checkSet);
        }
        Variable newVar = (Variable) Variable.get(prefixString + uniqueId++); 
        checkSet.add(newVar); 
        variables.add(newVar);
      }
      else { //here we set the replacement map because this branch
             //will always execute first for a given nonlinear
             //Variable.  It should not matter which substitution of
             //Terminals for a nonlinear Variable we choose
             //because the pattern won't match if they aren't equal!
        Variable newVar = (Variable) Variable.get(number + v.toString());
        variables.add(newVar);
        seen.add(v);
        replaceMap.put(v, newVar);
      }
    } 

    computeEqVariables(nonLinearVariableFinderMap);

    //System.out.println("!!!" + variables);
    //System.out.println("!!!" + eqVariables);

    rhsVariableCheck(seen, rhsVariables);
    replaceVariables(replaceMap);
  }

  private void computeEqVariables(
      HashMap<Variable, HashSet<Variable>> nonLinearVariableMap){
    for(HashSet<Variable> checkSet : nonLinearVariableMap.values()){
      ArrayList<Variable> retCheckSet 
        = new ArrayList<Variable>(checkSet);
      eqVariables.add(retCheckSet);
    }
  }

  private void rhsVariableCheck(HashSet<Variable> lhsVariables,
                                ArrayList<Variable> rhsVariables
      ){
    for(Variable v : rhsVariables){
      if(!lhsVariables.contains(v))
        throw new RuntimeException(
          "\nVariable in right hand side of rule:\n     "
        + this +                              "\n"
        + "does not appear in left hand side.\n");
    } 
  }

  //replace the original variables with the new uniquely
  //named variables computed in computeVariables
  private void replaceVariables(HashMap<Variable, Variable> replaceMap){
    //yea, this is a weird design pattern, but we want to loop through
    //each Variable in variables and replace the next Variable we seen
    //in the lhs with it, so we need to keep track of which is the next
    //Variable.  We do this by setting j outside of the loops
    int j = 0;
    for(Variable v : variables){
      while(j < lhs.size()){
        if(lhs.get(j) instanceof Variable){
          lhs.set(j++, v); //make sure to increment j when we break also
          break;
        } 
        ++j;
      }
    }
    //for the right hand side we just use the replaceMap
    //because it gives us the first instanc of a lhs variable
    //in the case where there are nonlinear variables.  Also
    //variables in the rhs need not appear in the same order
    //as those on the lhs, so the order of the variables
    //ArrayList cannot be used here as it can for the lhs
    for(int i = 0; i < rhs.size(); ++i){
      if(rhs.get(i) instanceof Variable){
        rhs.set(i, replaceMap.get(rhs.get(i)));
      }
    }
  }

  public Rule initial(){
    Symbol cursor = Cursor.get();
    Symbol begin = Begin.get();
    Rule ret = new Rule();
    ret.lhs = new Sequence();
    ret.lhs.add(begin);
    ret.lhs.add(cursor);
    for(int i = 1; i < lhs.size(); ++i){
        ret.lhs.add(lhs.get(i));
    }
    ret.rhs = rhs;
    ret.variables = variables;
    ret.eqVariables = eqVariables;
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
