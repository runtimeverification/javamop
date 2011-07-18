package logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.Iterator;
import java.lang.Math;

public class SetOperations {
  
   private static String binString(int binary, int digits) {
     String temp = Integer.toBinaryString(binary);
     int foundDigits = temp.length();
     String ret = temp;
     for (int i = foundDigits; i < digits; ++i) {
       ret = "0" + ret;
     }
     return ret;
   }  
  
  static <E> LinkedHashSet<LinkedHashSet<E>> 
    pow(E[] elements){
    LinkedHashSet<LinkedHashSet<E>> ret = new LinkedHashSet<LinkedHashSet<E>>();
    for(int i = 0 ; i < (int) Math.pow(2,elements.length); ++i){
      LinkedHashSet<E> subset = new LinkedHashSet<E>();
      String bitString = binString(i, elements.length); 
      for(int j = 0; j < bitString.length(); ++j){
         if(bitString.charAt(j) == '1'){
            subset.add(elements[j]);
         }
      }
      ret.add(subset);
    }
    return ret;
  }

  static <E> LinkedHashSet<E>
    intersect(LinkedHashSet<E> a, LinkedHashSet<E> b){
    //we want to iterate over the smaller set
    if(b.size() < a.size()){
      LinkedHashSet<E> tmp = a;
      a = b; b = tmp;
    }
    LinkedHashSet<E> ret = new LinkedHashSet<E>();
    for(E el : a){
      if(b.contains(el)) ret.add(el);
    }
    return ret;
  }

  static <E> LinkedHashSet<E>
    difference(LinkedHashSet<E> a, LinkedHashSet<E> b){
    LinkedHashSet<E> ret = new LinkedHashSet<E>();
    for(E el : a){
      if(!b.contains(el)) ret.add(el);
    }
    return ret;
  }

  static <E> boolean subset(LinkedHashSet<E> a, LinkedHashSet<E> b){
    for(E el : a){
      if(!b.contains(el)) return false;
    }
    return true;
  }

  static LinkedHashSet<GBAState>
   cross( LinkedHashSet<LinkedHashSet<LTLFormula>> a,
          LinkedHashSet<LinkedHashSet<LTLFormula>> b){
      LinkedHashSet<GBAState> ret
       = new LinkedHashSet<GBAState>(a.size() * b.size());

      for(LinkedHashSet<LTLFormula> ai : a){
        for(LinkedHashSet<LTLFormula> bi : b){
           ret.add(new GBAState(new LinkedHashSet<LTLFormula>(ai),
                                new LinkedHashSet<LTLFormula>(bi)));
        }
      }
      return ret;
   }

  static LTLFormula[] toLTLArray(LinkedHashSet<LTLFormula> in){
    LTLFormula[] ret = new LTLFormula[in.size()];
    int i = 0;
    for(LTLFormula l : in){
      ret[i] = l;
      ++i;
    }
    return ret;
  }

  public static void main(String[] args){
    Atom[] foo = {Atom.get("a"), Atom.get("b"), Atom.get("c")};
    
    System.out.println(pow(foo));
    
    LinkedHashSet<Atom> bar = new LinkedHashSet<Atom>();
    LinkedHashSet<Atom> car = new LinkedHashSet<Atom>();
    bar.add(Atom.get("a"));
    bar.add(Atom.get("b"));
    bar.add(Atom.get("c"));

    car.add(Atom.get("a"));
    car.add(Atom.get("b"));
    car.add(Atom.get("d"));

    System.out.println(intersect(bar,car));
    System.out.println(intersect(bar,new LinkedHashSet<Atom>()));
  }
}
