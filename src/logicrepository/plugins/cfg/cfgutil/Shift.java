package logicrepository.plugins.cfg.cfgutil;

import java.util.HashSet;
import java.util.HashMap;

class Shift extends LRAction {
   int target;
   Shift(int t) { target = t;}

   public int hashCode() { return target; }
   public boolean equals(Object o) {
      if (o == null) return false;
      if (!(o instanceof Shift)) return false;
      return target == (((Shift)o).target);
   }
   public String toString() { return "Shift "+target;}
   ActType type() { return ActType.SHIFT; }
}
