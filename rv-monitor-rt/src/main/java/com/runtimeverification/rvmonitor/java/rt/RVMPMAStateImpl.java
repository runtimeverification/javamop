package com.runtimeverification.rvmonitor.java.rt;

public class RVMPMAStateImpl {
  public int number;
  public int[] replacement;
  public int category;

  public RVMPMAStateImpl(int number){
    this.number = number;
    this.replacement = null;
    this.category = -1;
  }

  public RVMPMAStateImpl(int number, int[] replacement){
    this.number = number;
    this.replacement = replacement;
    this.category = -1;
  }

  public RVMPMAStateImpl(int number, int category){
    this.number = number;
    this.replacement = null;
    this.category = category;
  }
}
