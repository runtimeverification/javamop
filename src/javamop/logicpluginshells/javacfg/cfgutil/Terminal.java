package javamop.logicpluginshells.javacfg.cfgutil;

public class Terminal extends Symbol{
   public Terminal(String s){super(s); }
   Terminal(Symbol s){super(s.name); }
   public String toString(){return "t("+name+")";}
}
