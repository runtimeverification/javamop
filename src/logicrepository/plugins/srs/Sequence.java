package logicrepository.plugins.srs;

import java.util.ArrayList;

public class Sequence extends ArrayList<Symbol> {

  public Sequence(int size){
    super(size);
  }

  public Sequence(){
    super();
  }

  public Sequence(ArrayList<Symbol> symbols){
    for(Symbol s : symbols){
      if(s == null) continue;
      add(s);
    }
    if(size() == 0) add(Epsilon.get());
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Symbol s : this){
      sb.append(s.toString());
      sb.append(" ");
    }
    return sb.toString();
  }

  public Sequence getTerminals(){
    Sequence ret = new Sequence();
    for(Symbol s : this){
      if(s instanceof Terminal){
        ret.add(s);
      }
    }
    return ret;
  }

  public Sequence getVariables(){
    Sequence ret = new Sequence();
    for(Symbol s : this){
      if(s instanceof Variable){
        ret.add(s);
      }
    }
    return ret;
  }
 
  // All this does now is remove adjacent Variables
  // because adjacent Variables are unnecessary 
//  public Sequence simplify(){
//    ArrayList<Symbol> symbols = new ArrayList<Symbol>();
//    for(Symbol s : this){
//      symbols.add(s);
//    }
//    for(int i = 0; i < size() - 1; ++i){
//      if((symbols.get(i) instanceof Variable) && (symbols.get(i + 1) instanceof Variable))
//        symbols.set(i, null);
//    }
//
//    return new Sequence(symbols);
//  }

  // attempts to generate a new sequence with the cursor
  // in the original sequence advanced.  
  // If the next Symbol in the pattern is a Variable and the Terminal
  // after it matches s, we need to generate two Sequences, one where
  // we "stay in the Variable" and one where we assume the Variable matches
  // \epsilon and carry on  
  public Sequence[] advance(Symbol s){
    int i;
    for(i = 0; i < size(); ++i){
      if(get(i) instanceof Cursor) break; 
    }
    if(i == size()) return null; //if there is no Cursor return null
    if(i + 1 == size()) {
      return new Sequence[]{ copy() }; //if we are already at the end return a copy
    }
    Symbol next = get(i + 1);
    if(next instanceof Variable){
      Symbol afterNext = get(i + 2);
      if(afterNext != s) {
        return new Sequence[] { copy() };
      }
      Sequence advanced = new Sequence(size());
      for(int j = 0; j < i; ++j){
        advanced.add(get(j));
      }
      advanced.add(next);
      advanced.add(afterNext);
      advanced.add(Cursor.get());
      for(int j = i + 3; j < size(); ++j){
        advanced.add(get(j));
      }
      return new Sequence[]{ copy(), advanced };
    }
    assert(next instanceof Terminal);
    if(next != s) return null;
    Sequence advanced = new Sequence(size());
    for(int j = 0; j < i; ++j){
      advanced.add(get(j));
    }
    advanced.add(next);
    advanced.add(Cursor.get());
    for(int j = i + 2; j < size(); ++j) {
      advanced.add(get(j));
    }
    return new Sequence[] { advanced };
  }

  public Sequence copy(){
    Sequence ret = new Sequence(size());
    for(Symbol s : this){
      ret.add(s);
    }
    return ret;
  }

  public static void printSequenceArray(Sequence[] arr){
    for(Sequence s : arr){
      System.out.print(s);
      System.out.print("; ");
    }
    System.out.println();
  }

  public static void main(String[] args){
    Sequence s = new Sequence();
    s.add(Cursor.get());
    s.add(Terminal.get("a"));
    s.add(Terminal.get("b"));
    Sequence[] n = s.advance(Symbol.get("b"));
    System.out.println(n);
    n = s.advance(Symbol.get("a"));
    printSequenceArray(n);
    n = s.advance(Symbol.get("a"))[0].advance(Symbol.get("b"));
    printSequenceArray(n);

    s = new Sequence();
    s.add(Cursor.get());
    s.add(Variable.get("A"));
    s.add(Terminal.get("a"));
    s.add(Terminal.get("b"));

    n = s.advance(Symbol.get("b"));
    printSequenceArray(n);
    n = s.advance(Symbol.get("a"));
    printSequenceArray(n);
  }
}
