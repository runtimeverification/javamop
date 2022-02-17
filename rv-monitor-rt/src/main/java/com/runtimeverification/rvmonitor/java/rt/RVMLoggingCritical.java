package com.runtimeverification.rvmonitor.java.rt;

import java.io.PrintStream;

public class RVMLoggingCritical extends RVMLogging {
  PrintStream p;
  
  public RVMLoggingCritical(PrintStream ps) {
    p = ps;
  }
  
  public void println(Level l) { if(l.compareTo(Level.CRITICAL) >= 0) p.println(); }
  public void print(Level l, boolean x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, boolean x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, char x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, char x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, char[] x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, char[] x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, double x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, double x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, float x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, float x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, int x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, int x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, long x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, long x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, Object x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, Object x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void print(Level l, String x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  public void println(Level l, String x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
}

