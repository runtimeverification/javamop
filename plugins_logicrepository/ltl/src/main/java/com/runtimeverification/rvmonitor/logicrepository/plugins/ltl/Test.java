package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ltl.parser.LTLParser;

public class Test{
    public static void main(String[] args){
        //can add extra atoms by creating them
        //without using them
        
        
        LTLParser ltlParser = LTLParser.parse(args[0]);
        
        
        LTLFormula f = ltlParser.getFormula();
        //    System.out.println("====formula parsed as:");
        //    System.out.println(f);
        //    System.out.println("");
        //    System.out.println("====formula simplifies to:");
        f = f.simplify();
        //    System.out.println(f);
        //   System.out.println("");
        
        //    System.out.println("====Alternating Automata for formula:");
        AAutomaton aa = new AAutomaton(f);    
        //    System.out.println(aa);
        
        //    System.out.println("====GBA for formula:");
        GBA gba = new GBA(aa);
        //    System.out.println(gba);
        
        //   System.out.println("====BA for formula:");
        BA ba = new BA(gba);
        //   System.out.println(ba);
        
        //   System.out.println("=====NFA for formula:");
        NFA nfa = new NFA(ba);
        //   System.out.println(nfa);
        
        //   System.out.println("=====DFA for formula:");
        DFA dfa = new DFA(nfa);
        System.out.println(dfa); 
        
        /*
         *    ArrayList<LTLFormula> l = new ArrayList<LTLFormula>();
         *    l.add(f);
         *    l.add(new Negation(f).simplify());
         *    l.add(Atom.get("a"));
         *    l.add(Atom.get("d"));
         *    l.add(Atom.get("b"));
         *    l.add(True.get());
         *    l.add(False.get());
         *    l.add(new Negation(False.get()).simplify());
         *    l.add(new And(Atom.get("b"), Atom.get("b")));
         *    l.add(new And(Atom.get("a"), Atom.get("a")));
         *    Collections.sort(l);
         *    System.out.println(l);*/
        //LTLFormula f = new Negation(new Negation(new Next(Atom.get("'foo"))));
        //System.out.println(f);
        /*  //LTLFormula f = get("'foo");
         *    //LTLFormula q = get("'bar");
         *    LTLFormula p = new Negation(new XOr(new Negation(new Next(f)), f));
         *    LTLFormula c = new Negation(new Until(new Negation(new Previously(new Negation(new And(True.get(), p)))), True.get()));
         *    //LTLFormula c 
         * //= new Or(False.get(), new And(True.get(), new And(True.get(), Atom.get("bar"))));
         *    System.out.print("red\n    ");
         *    c.print(System.out);
         *    System.out.println("\n== ");
         *    LTLFormula foo = c.simplify();
         *    System.out.print("    ");
         *    foo.print(System.out);
         *    System.out.println("\n . ");
         *    System.out.println(f);*/
        
        //System.out.println(Atom.get("bar").equals(Atom.get("bar")));
        //System.out.println(Atom.get("bar").equals(Atom.get("bar")));
        //System.out.println(Atom.get("bar").equals(False.get()));
        //System.out.println(False.get().equals(False.get()));
    }
}
