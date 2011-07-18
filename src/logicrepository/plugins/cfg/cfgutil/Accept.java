package logicrepository.plugins.cfg.cfgutil;

class Accept extends LRAction {
   public int hashCode() { return 1;}
   public boolean equals(Object o) {
      if (o == null) return false;
      if (!(o instanceof Accept)) return false;
      return true;
   }
   public String toString() { return "Accept";}
   ActType type() { return ActType.ACCEPT; }
}
