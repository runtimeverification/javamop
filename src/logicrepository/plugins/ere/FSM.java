package logicrepository.plugins.ere;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.PrintStream;
import java.util.LinkedHashSet;

public class FSM {
  public LinkedHashMap<ERE, LinkedHashMap<Symbol, ERE>> contents;
  public LinkedHashSet<ERE> match; 
  private int count = 0;
  private LinkedHashMap<ERE, String> number;
  private ERE start;
  private Symbol[] events;

  static public FSM get(ERE input, Symbol[] events){
    return new FSM(input, events);
  }

  private FSM(ERE input, Symbol[] events){
    start = input;
	 contents = new LinkedHashMap<ERE, LinkedHashMap<Symbol, ERE>>();
	 match = new LinkedHashSet<ERE>();
    this.events = events;
	 number = new LinkedHashMap<ERE, String>();
	 generate(start);
  }

  private void generate(ERE state){
	 number.put(state, "s" + count++);
	 LinkedHashMap<Symbol, ERE> trans = new LinkedHashMap<Symbol, ERE>();
	 if(state.containsEpsilon()){
      match.add(state);
	 }
	 contents.put(state, trans);
    for(Symbol event : events){
      ERE next = state.derive(event);
	   if(next == Empty.get()) continue;
		trans.put(event, next);	
		if(contents.containsKey(next)) continue;
      generate(next); 
	 }
  }

  public void print(PrintStream p){
	 p.println("s0 [");
	 printTransition(contents.get(start), p);
	 p.println("]");
    for(ERE state : contents.keySet()){
		if(state == start) continue;
		p.println(number.get(state) + " [");
      printTransition(contents.get(state), p);
		p.println("]");
	 }
	 if(match.size() == 0) return;
	 p.print("alias match = ");
	 for(ERE state : match){
      p.print(number.get(state) + " ");
	 }
	 p.println("");
	 
	 // nonmatch alias for enforcement
	 p.print("alias nonmatch = ");
	 for(ERE state : contents.keySet()){
		 if (!match.contains(state)) {
			 p.print(number.get(state) + " ");
		 }
	 }
	 p.println("");
  }

  private void printTransition(LinkedHashMap<Symbol, ERE> trans, PrintStream p){
    for(Symbol s : trans.keySet()){
      p.println("   " + s + " -> " + number.get(trans.get(s)));
	 }
  }
}
