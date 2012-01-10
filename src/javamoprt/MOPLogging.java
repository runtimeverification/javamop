package javamoprt;

import java.io.PrintStream;

public abstract class MOPLogging {
  public static enum Level {
    ALL, WARNING, CRITICAL, NONE
  }

  public static MOPLogging out;
  public static MOPLogging err;

  static {
    String loggingLevel = System.getenv("MOPLOGGINGLEVEL");
   
    if(loggingLevel == null){
      out = new MOPLoggingAll(System.out);
      err = new MOPLoggingAll(System.err);
    } 
    else if(loggingLevel.equals("ALL")){
      out = new MOPLoggingAll(System.out);
      err = new MOPLoggingAll(System.err);
    }
    else if(loggingLevel.equals("WARNING")){
      out = new MOPLoggingWarning(System.out);
      err = new MOPLoggingWarning(System.err);
    }
    else if(loggingLevel.equals("CRITICAL")){
      out = new MOPLoggingCritical(System.out);
      err = new MOPLoggingCritical(System.err);
    }
    else if(loggingLevel.equals("NONE")){
      out = new MOPLoggingNone(System.out);
      err = new MOPLoggingNone(System.err);
    }
  }
 
  abstract void println(Level l);
  abstract void print(Level l, boolean x);
  abstract void println(Level l, boolean x);
  abstract void print(Level l, char x);
  abstract void println(Level l, char x);
  abstract void print(Level l, char[] x);
  abstract void println(Level l, char[] x);
  abstract void print(Level l, double x);
  abstract void println(Level l, double x);
  abstract void print(Level l, float x);
  abstract void println(Level l, float x);
  abstract void print(Level l, int x);
  abstract void println(Level l, int x);
  abstract void print(Level l, long x);
  abstract void println(Level l, long x);
  abstract void print(Level l, Object x);
  abstract void println(Level l, Object x);
  abstract void print(Level l, String x);
  abstract void println(Level l, String x);

  void println(){ println(Level.ALL); }
  void print(boolean x){ println(Level.ALL, x); }
  void println(boolean x){ println(Level.ALL, x); }
  void print(char x){ println(Level.ALL, x); }
  void println(char x){ println(Level.ALL, x); }
  void print(char[] x){ println(Level.ALL, x); }
  void println(char[] x){ println(Level.ALL, x); }
  void print(double x){ println(Level.ALL, x); }
  void println(double x){ println(Level.ALL, x); }
  void print(float x){ println(Level.ALL, x); }
  void println(float x){ println(Level.ALL, x); }
  void print(int x){ println(Level.ALL, x); }
  void println(int x){ println(Level.ALL, x); }
  void print(long x){ println(Level.ALL, x); }
  void println(long x){ println(Level.ALL, x); }
  void print(Object x){ println(Level.ALL, x); }
  void println(Object x){ println(Level.ALL, x); }
  void print(String x){ print(Level.ALL, x); }
  void println(String x){ println(Level.ALL, x); }

  public static void main(String[] args){
    System.out.println("The following levels are enabled by your environment:");
    out.println(Level.ALL, "ALL");
    out.println(Level.WARNING, "WARNING");
    out.println(Level.CRITICAL, "CRITICAL");
  }

}

class MOPLoggingAll extends MOPLogging {
  PrintStream p;
  MOPLoggingAll(PrintStream ps) {
    p = ps;
  }
  void println(Level l) { p.println(); }
  void print(Level l, boolean x){ p.println(x); }
  void println(Level l, boolean x){ p.println(x); }
  void print(Level l, char x){ p.println(x); }
  void println(Level l, char x){ p.println(x); }
  void print(Level l, char[] x){ p.println(x); }
  void println(Level l, char[] x){ p.println(x); }
  void print(Level l, double x){ p.println(x); }
  void println(Level l, double x){ p.println(x); }
  void print(Level l, float x){ p.println(x); }
  void println(Level l, float x){ p.println(x); }
  void print(Level l, int x){ p.println(x); }
  void println(Level l, int x){ p.println(x); }
  void print(Level l, long x){ p.println(x); }
  void println(Level l, long x){ p.println(x); }
  void print(Level l, Object x){ p.println(x); }
  void println(Level l, Object x){ p.println(x); }
  void print(Level l, String x){ p.println(x); }
  void println(Level l, String x){ p.println(x); }
}

class MOPLoggingWarning extends MOPLogging {
  PrintStream p;
  MOPLoggingWarning(PrintStream ps) {
    p = ps;
  }
  void println(Level l) { if(l.compareTo(Level.WARNING) >= 0) p.println(); }
  void print(Level l, boolean x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, boolean x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, char x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, char x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, char[] x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, char[] x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, double x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, double x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, float x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, float x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, int x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, int x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, long x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, long x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, Object x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, Object x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void print(Level l, String x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
  void println(Level l, String x){ if(l.compareTo(Level.WARNING) >= 0) p.println(x); }
}

class MOPLoggingCritical extends MOPLogging {
  PrintStream p;
  MOPLoggingCritical(PrintStream ps) {
    p = ps;
  }
  void println(Level l) { if(l.compareTo(Level.CRITICAL) >= 0) p.println(); }
  void print(Level l, boolean x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, boolean x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, char x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, char x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, char[] x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, char[] x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, double x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, double x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, float x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, float x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, int x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, int x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, long x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, long x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, Object x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, Object x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void print(Level l, String x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
  void println(Level l, String x){ if(l.compareTo(Level.CRITICAL) >= 0) p.println(x); }
}

class MOPLoggingNone extends MOPLogging {
  MOPLoggingNone(PrintStream ps){}
  void println(Level l) {}
  void print(Level l, boolean x){}
  void println(Level l, boolean x){}
  void print(Level l, char x){}
  void println(Level l, char x){}
  void print(Level l, char[] x){}
  void println(Level l, char[] x){}
  void print(Level l, double x){}
  void println(Level l, double x){}
  void print(Level l, float x){}
  void println(Level l, float x){}
  void print(Level l, int x){}
  void println(Level l, int x){}
  void print(Level l, long x){}
  void println(Level l, long x){}
  void print(Level l, Object x){}
  void println(Level l, Object x){}
  void print(Level l, String x){}
  void println(Level l, String x){}
}

