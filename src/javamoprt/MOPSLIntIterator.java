package javamoprt;

import java.util.Collection;

public interface MOPSLIntIterator {
  public MOPSLIntIterator copy();
  public boolean next();
  public boolean next(int amount);
  public boolean previous();
  public boolean previous(int amount);
  public int get();
  public void splice(MOPSLIntIterator end, MOPIntSpliceList replacement);
  public void nonDestructiveSplice(MOPSLIntIterator end, MOPIntSpliceList replacement);
  public void nonDestructiveSplice(MOPSLIntIterator end, int[] replacement);
}

