package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.lang.Math;

public class SetOperations {
    
    /**
     * A binary string of an integer with a minimum number of digits. Fill in additional digits 
     * as zeroes at the front if necessary.
     * @param binary The integer to convert to binary.
     * @param digits The minimum number of digits.
     * @return A binary string representing {@code binary} of at least length {@code digits}.
     */
    private static String binString(int binary, int digits) {
        String temp = Integer.toBinaryString(binary);
        int foundDigits = temp.length();
        String ret = temp;
        for (int i = foundDigits; i < digits; ++i) {
            ret = "0" + ret;
        }
        return ret;
    }  
    
    /**
     * The power set of an array of elements.
     * @param elements An array of elements.
     * @return The power set of the elements, as a {@link LinkedHashSet}.
     */
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
    
    /**
     * The intersection of two sets. That is, the set of elements that are in both of two other
     * sets of elements.
     * @param a One side of the intersection.
     * @param b The other side of the intersection.
     * @return The intersection of {@code a} and {@code b} as a {@link LinkedHashSet}.
     */
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
    
    /**
     * The difference of two sets. That is, the elements of one set that are not in another set.
     * @param a The set to remove elements from.
     * @param b The set with the elements to remove.
     * @return The difference of {@code a} and {@code b} as a {@link LinkedHashSet}.
     */
    static <E> LinkedHashSet<E>
    difference(LinkedHashSet<E> a, LinkedHashSet<E> b){
        LinkedHashSet<E> ret = new LinkedHashSet<E>();
        for(E el : a){
            if(!b.contains(el)) ret.add(el);
        }
        return ret;
    }
    
    /**
     * Test if one set is a subset of another. That is, whether one set only has elements that
     * are present in another set.
     * @param a The set to test for being a subset.
     * @param b The set to test for being a superset.
     * @return If {@code a} is a subset of {@code b}.
     */
    static <E> boolean subset(LinkedHashSet<E> a, LinkedHashSet<E> b){
        for(E el : a){
            if(!b.contains(el)) return false;
        }
        return true;
    }
    
    /**
     * The cross product or cartesian product of two sets. That is, the set of all pairs with
     * the first member being any element from the first set, and the second member being any
     * element from the second set.
     * @param a The set populating the first member of the pairs.
     * @param b The set populating the second member of the pairs.
     * @return A set of pairs of members of {@code a} and {@code b}.
     */
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
    
    /**
     * Convert a {@link LTLFormula} {@link LinkedHashSet} into an array.
     * @param in The {@code LinkedHashSet} to convert.
     * @return An array of {@link LTLFormula}s.
     */
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
