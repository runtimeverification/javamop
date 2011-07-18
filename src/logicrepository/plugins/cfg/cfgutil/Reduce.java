package logicrepository.plugins.cfg.cfgutil;

import java.util.ArrayList;

class Reduce extends LRAction {
   int nt;
   int size;
   Reduce(int oldnt, int oldsize) { nt = oldnt; size = oldsize;}

   public int hashCode() { return nt+size;}
   public boolean equals(Object o) {
      if (o == null) return false;
      if (!(o instanceof Reduce)) return false;
      return nt == (((Reduce)o).nt) && size == (((Reduce)o).size);
   }
   public String toString() { return "Reduce "+nt +" "+size;}
   ActType type() { return ActType.REDUCE; }
}
