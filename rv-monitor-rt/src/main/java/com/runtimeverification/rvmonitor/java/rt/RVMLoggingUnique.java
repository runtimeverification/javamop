package com.runtimeverification.rvmonitor.java.rt;

import java.io.PrintStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class RVMLoggingUnique extends RVMLogging {
  PrintStream p;
  Set<String> msgs;
  Map<String, Integer> counts;
  RVMLoggingUnique(PrintStream ps) {
    p = ps;
    counts = new ConcurrentHashMap<>();
    Runtime.getRuntime().addShutdownHook( new Thread() {
            public void run()
            {
                File file = new File(System.getProperty("user.dir")
                                     + File.separator + "violation-counts");
                try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true)) {
                    for (String msg : counts.keySet()) {
                        if (msg.startsWith("Specification")) {
                            fw.write( counts.get(msg) + " " + msg + System.lineSeparator());
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
    });
  }

  public void println(Level l) { p.println(); }
  public void print(Level l, boolean x){ p.println(x); }
  public void println(Level l, boolean x){ p.println(x); }
  public void print(Level l, char x){ p.println(x); }
  public void println(Level l, char x){ p.println(x); }
  public void print(Level l, char[] x){ p.println(x); }
  public void println(Level l, char[] x){ p.println(x); }
  public void print(Level l, double x){ p.println(x); }
  public void println(Level l, double x){ p.println(x); }
  public void print(Level l, float x){ p.println(x); }
  public void println(Level l, float x){ p.println(x); }
  public void print(Level l, int x){ p.println(x); }
  public void println(Level l, int x){ p.println(x); }
  public void print(Level l, long x){ p.println(x); }
  public void println(Level l, long x){ p.println(x); }
  public void print(Level l, Object x){ p.println(x); }
  public void println(Level l, Object x){ p.println(x); }
  public void print(Level l, String x){ p.println(x); }
  public void println(Level l, String x){ handleStringPrinting(x); }

  public void handleStringPrinting(String x) {
    if (counts.containsKey(x)) {
        counts.put(x, counts.get(x) + 1);
    } else {
      p.println(x);
      counts.put(x, 1);
    }
 }
}
