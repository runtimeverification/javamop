package logicrepository.plugins.cfg.cfgutil;

public class Terminal extends Symbol{
   public Terminal(String s){super(s); }
   Terminal(Symbol s){super(s.name); }
   public String toString(){return name;}
}
