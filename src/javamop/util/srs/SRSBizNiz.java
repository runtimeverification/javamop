/*
  SRSBizNiz is a String Rewrite System library for Java.
  Rules are given as pairs of strings, where the first element is a regex, and the second is a replacement.
  Currently a naive implementation, and rules are applied (everywhere) from first to last over a single pass,
  and a normal form is reached by doing such passes until convergence.

  Created by Michael Ilseman
*/
package javamop.util.srs;

import java.util.ArrayList;

public class SRSBizNiz {
  private String internalState;
  private ArrayList< Pair<String, String> > rules = new ArrayList() ;

  public SRSBizNiz(String initial) {
    internalState = initial;
  }

  public SRSBizNiz() {
    internalState = "";
  }

  // Append to the end of the internal string
  public void append(Object o) {
    internalState = internalState + o.toString();
  }

  // Add a new rule
  public void addRule(String lhs, String rhs) {
    rules.add(new Pair(lhs,rhs));
  }

  // Make a single pass over the rules, applying them (everywhere) in order
  public void makePass() {
    for (Pair<String,String> p : rules) {
      internalState = internalState.replaceAll(p.fst, p.snd);
    }
  }

  // Go to a normal form, i.e. keep rewriting until a fixed point is reached.
  public void toNormalForm() {
    String tmp = internalState;
    makePass();
    if (tmp.equals(internalState))
      return;
    else
      toNormalForm();
  }


  public String toString() {
    return internalState;
  }

  public void print() {
    System.out.println(internalState);
  }

}

class Pair<A,B> {
  public A fst; //public for now since I hate adding ()
  public B snd; //public for now since I hate adding ()

  Pair(A l, B r) {
    fst = l;
    snd = r;
  }

  public A fst() {
    return fst;
  }

  public B snd() {
    return snd;
  }
}