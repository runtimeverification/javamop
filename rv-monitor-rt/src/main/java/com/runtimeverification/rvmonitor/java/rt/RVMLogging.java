package com.runtimeverification.rvmonitor.java.rt;

public abstract class RVMLogging {
  public static enum Level {
    ALL, WARNING, CRITICAL, NONE, UNIQUE
  }

  public static RVMLogging out;
  public static RVMLogging err;

  static {
    String loggingLevel = System.getenv("RVMLOGGINGLEVEL");

    if(loggingLevel == null){
      out = new RVMLoggingAll(System.out);
      err = new RVMLoggingAll(System.err);
    }
    else if(loggingLevel.equals("ALL")){
      out = new RVMLoggingAll(System.out);
      err = new RVMLoggingAll(System.err);
    }
    else if(loggingLevel.equals("WARNING")){
      out = new RVMLoggingWarning(System.out);
      err = new RVMLoggingWarning(System.err);
    }
    else if(loggingLevel.equals("CRITICAL")){
      out = new RVMLoggingCritical(System.out);
      err = new RVMLoggingCritical(System.err);
    }
    else if(loggingLevel.equals("NONE")){
      out = new RVMLoggingNone(System.out);
      err = new RVMLoggingNone(System.err);
    }
    else if(loggingLevel.equals("UNIQUE")){
      out = new RVMLoggingUnique(System.out);
      err = new RVMLoggingUnique(System.err);
    }
  }

  public abstract void println(Level l);
  public abstract void print(Level l, boolean x);
  public abstract void println(Level l, boolean x);
  public abstract void print(Level l, char x);
  public abstract void println(Level l, char x);
  public abstract void print(Level l, char[] x);
  public abstract void println(Level l, char[] x);
  public abstract void print(Level l, double x);
  public abstract void println(Level l, double x);
  public abstract void print(Level l, float x);
  public abstract void println(Level l, float x);
  public abstract void print(Level l, int x);
  public abstract void println(Level l, int x);
  public abstract void print(Level l, long x);
  public abstract void println(Level l, long x);
  public abstract void print(Level l, Object x);
  public abstract void println(Level l, Object x);
  public abstract void print(Level l, String x);
  public abstract void println(Level l, String x);

  public void println(){ println(Level.ALL); }
  public void print(boolean x){ println(Level.ALL, x); }
  public void println(boolean x){ println(Level.ALL, x); }
  public void print(char x){ println(Level.ALL, x); }
  public void println(char x){ println(Level.ALL, x); }
  public void print(char[] x){ println(Level.ALL, x); }
  public void println(char[] x){ println(Level.ALL, x); }
  public void print(double x){ println(Level.ALL, x); }
  public void println(double x){ println(Level.ALL, x); }
  public void print(float x){ println(Level.ALL, x); }
  public void println(float x){ println(Level.ALL, x); }
  public void print(int x){ println(Level.ALL, x); }
  public void println(int x){ println(Level.ALL, x); }
  public void print(long x){ println(Level.ALL, x); }
  public void println(long x){ println(Level.ALL, x); }
  public void print(Object x){ println(Level.ALL, x); }
  public void println(Object x){ println(Level.ALL, x); }
  public void print(String x){ print(Level.ALL, x); }
  public void println(String x){ println(Level.ALL, x); }

  public static void main(String[] args){
    System.out.println("The following levels are enabled by your environment:");
    out.println(Level.ALL, "ALL");
    out.println(Level.WARNING, "WARNING");
    out.println(Level.CRITICAL, "CRITICAL");
    out.println(Level.UNIQUE, "UNIQUE");
  }

}




