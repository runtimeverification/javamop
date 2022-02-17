package com.runtimeverification.rvmonitor.java.rt;

public interface RVMSLIntIterator {
  public RVMSLIntIterator copy();
  public boolean next();
  public boolean next(int amount);
  public boolean previous();
  public boolean previous(int amount);
  public int get();
  public void splice(RVMSLIntIterator end, RVMIntSpliceList replacement);
  public void nonDestructiveSplice(RVMSLIntIterator end, RVMIntSpliceList replacement);
  public void nonDestructiveSplice(RVMSLIntIterator end, int[] replacement);
}

