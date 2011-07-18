package javamop.logicpluginshells.javacfg.cfgutil;

public class Symbol implements java.io.Serializable {
   public final String name;

   Symbol(String s){ name = s;}
   public String toString(){return "sym("+name+")";}
   public int hashCode(){ return name.hashCode(); }
   public boolean equals(Object o){
      if (o == null) return false;
      if (!(o instanceof Symbol)) return false;
      Symbol s = (Symbol) o;
      return (name.equals(s.name));
   }
}
