package logicrepository.plugins.cfg.cfgutil;

public class NonTerminal extends Symbol{
   NonTerminal(String s){super(s);}
   NonTerminal(Symbol s){super(s.name);}
   public String toString(){return "nt("+name+")";}
}
