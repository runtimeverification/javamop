package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.lang.reflect.Array;
import java.util.*;

// NB this does not generate LR tables in their full generality merely for epsilon free CFGs
public class LR implements java.io.Serializable {
    HashSet<LRAction>[][] at;
    int[][] gt;
    HashMap<NonTerminal, Integer> nonTermMap = new HashMap<NonTerminal, Integer>();
    int start;
    int q0 = 0;

    LR(LR old) {
        this(old.at, old.gt, old.start, old.q0);
    }

    LR(HashSet<LRAction>[][] atold, int[][] gtold, int startold, int qold) {
        at = DeepCopy.copy(atold);
        gt = DeepCopy.copy(gtold);
        start = startold;
        q0 = qold;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(at) + Arrays.deepHashCode(gt);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof LR))
            return false;
        return at.equals(((LR) o).at) && gt.equals(((LR) o).gt)
                && start == (((LR) o).start) && q0 == (((LR) o).q0);
    }

    @Override
    public String toString() {
        String ret = "Start: " + start + ", " + q0;
        ret += "\nNonTermMap: \n" + nonTermMap.toString();
        ret += "\nAction table:\n";
        for (int i = 0; i < at.length; i++) {
            ret += Integer.toString(i);
            for (int j = 0; j < at[0].length; j++) {
                ret += " " + at[i][j];
            }
            ret += "\n";
        }

        ret += "\nGoto table:\n";
        for (int i = 0; i < gt.length; i++) {
            ret += Integer.toString(i);
            for (int j = 0; j < gt[0].length; j++) {
                ret += " " + gt[i][j];
            }
            ret += "\n";
        }
        return ret;
    }

    // The layout of the ActionTable is as follows all mapped to integers
    // [State][Terminal][ActionSet]
    //
    // The layout of a given ActionSet is [LRAction] where Shift k |-> [k],
    // Reduce x y |-> [x,2*y], Accept |-> []
    public int[][][][] atArray() {
        int[][][][] ret = new int[at.length][at[0].length - 1][][];
        for (int i = 0; i < at.length; i++) {
            for (int j = 1; j < at[0].length; j++) {
                ret[i][j - 1] = new int[at[i][j].size()][];
                int k = 0;
                for (LRAction a : at[i][j]) {
                    switch (a.type()) {
                    case SHIFT:
                        ret[i][j - 1][k] = new int[1];
                        ret[i][j - 1][k][0] = ((Shift) a).target;
                        break;
                    case REDUCE:
                        ret[i][j - 1][k] = new int[2];
                        ret[i][j - 1][k][0] = ((Reduce) a).nt;
                        ret[i][j - 1][k][1] = ((Reduce) a).size;
                        break;
                    }
                    k++;
                }
            }
        }
        return ret;
    }

    public boolean[] accArray() {
        boolean[] ret = new boolean[at.length];
        for (int i = 0; i < at.length; i++)
            if (at[i][0].isEmpty())
                ret[i] = false;
            else
                ret[i] = true;
        return ret;
    }

    public String accString() {
        boolean[] acc = accArray();
        String ret = "{ ";
        for (int i = 0; i < acc.length; i++)
            ret += Boolean.toString(acc[i]) + ", ";
        return ret + "}";
    }

    public String caccString() {
        boolean[] acc = accArray();
        String ret = "static int __RV_acc[" + acc.length + "] = { ";
        for (int i = 0; i < acc.length; i++)
            ret += ((acc[i]) ? 1 : 0) + ", ";
        return ret + "};\n";
    }

    public String atString() {
        int[][][][] ata = atArray();
        String ret = "{ ";
        for (int i = 0; i < ata.length; i++) {
            ret += "{ ";
            for (int j = 0; j < ata[i].length; j++) {
                ret += "{ ";
                for (int k = 0; k < ata[i][j].length; k++) {
                    ret += "{ ";
                    if (ata[i][j][k] != null) {
                        for (int l = 0; l < ata[i][j][k].length; l++) {
                            ret += Integer.toString(ata[i][j][k][l]);
                            ret += ", ";
                        }
                    }
                    ret += " }, ";
                }
                ret += " }, ";
            }
            ret += " }, ";
        }
        ret += " };\n";
        return ret;
    }

    public String catString() {
        int[][][][] ata = atArray();
        int l1 = ata.length;
        int l2 = 0;
        int l3 = 0;
        for (int i = 0; i < l1; ++i) {
            l2 = Math.max(l2, ata[i].length);
            for (int j = 0; j < ata[i].length; ++j) {
                l3 = Math.max(l3, ata[i][j].length);
            }
        }
        String ret = "static int __RV_at[" + l1 + "][" + l2 + "][" + l3
                + "][3] = ";
        return ret + catStringAux();
    }

    public String catStringAux() {
        int[][][][] ata = atArray();
        String ret = "{ ";
        for (int i = 0; i < ata.length; i++) {
            ret += "{ ";
            for (int j = 0; j < ata[i].length; j++) {
                ret += "{ ";
                for (int k = 0; k < ata[i][j].length; k++) {
                    ret += "{ ";
                    if (ata[i][j][k] != null) {
                        ret += Integer.toString(ata[i][j][k].length) + ", ";
                        for (int l = 0; l < ata[i][j][k].length; l++) {
                            ret += Integer.toString(ata[i][j][k][l]);
                            ret += ", ";
                        }
                    } else {
                        ret += "0,";
                    }
                    ret += " }, ";
                }
                ret += " }, ";
            }
            ret += " }, ";
        }
        ret += " };\n";
        return ret;
    }

    public String gtString() {
        String ret = "{ ";
        for (int i = 0; i < gt.length; i++) {
            ret += "{ ";
            for (int j = 0; j < gt[i].length; j++) {
                ret += Integer.toString(gt[i][j]);
                ret += ", ";
            }
            ret += " }, ";
        }
        ret += " };\n";
        return ret;
    }

    public String cgtString() {
        String ret = "static int __RV_gt[" + gt.length + "][" + gt[0].length
                + "] = ";
        return ret + gtString();
    }

    // CT p128
    public LR(CFG g, HashMap<Terminal, Integer> termMap) {
        HashMap<HashSet<LRPair>, HashMap<Terminal, HashSet<LRAction>>> atsparse = new HashMap<HashSet<LRPair>, HashMap<Terminal, HashSet<LRAction>>>();
        HashMap<HashSet<LRPair>, HashMap<NonTerminal, HashSet<LRPair>>> gtsparse = new HashMap<HashSet<LRPair>, HashMap<NonTerminal, HashSet<LRPair>>>();
        HashMap<HashSet<LRPair>, Integer> ccMap = new HashMap<HashSet<LRPair>, Integer>();
        HashSet<HashSet<LRPair>> ccSet = mkCC(g);
        Terminal t;
        NonTerminal nt;
        HashSet<NonTerminal> safents = g.nonTerminals();
        safents.remove(g.start);

        // Populate the integer maps
        // NB -1 indicates errors
        int hashint = 0;
        for (HashSet<LRPair> cc : ccSet)
            ccMap.put(cc, hashint++);
        hashint = 0;
        for (NonTerminal nont : g.nonTerminals())
            nonTermMap.put(nont, hashint++);
        termMap.put(new EOF(), 0);

        // Calculate the goal production
        Production goalprod = new Production(g.start, new ArrayList<Symbol>());
        HashSet<LRPair> goallrps = new HashSet<LRPair>();
        HashSet<LRPair> temp;
        for (Production p : g.prodsOf(g.start))
            goalprod = p.clone();
        goalprod.rhs.add(0, new Cursor());
        goallrps.add(new LRPair(goalprod, new EOF()));

        q0 = ccMap.get(closure(goallrps, g));

        // Initialze the action table to be empty
        for (HashSet<LRPair> cc : ccSet) {
            atsparse.put(cc, new HashMap<Terminal, HashSet<LRAction>>());
            for (Terminal terminal : termMap.keySet()) {
                atsparse.get(cc).put(terminal, new HashSet<LRAction>());
            }
        }

        // the production to accept on
        Production accprod = null;
        for (Production p : g.prodsOf(g.start))
            accprod = p.clone();
        accprod.rhs.add(new Cursor());
        LRPair acclrp = new LRPair(accprod, new EOF());

        // Fill the action table
        for (HashSet<LRPair> cc : ccSet) {
            for (LRPair lrp : cc) {
                if (lrp.isTAfterCursor()) {
                    t = (Terminal) lrp.getAfterCursor();
                    Shift ss = new Shift(ccMap.get(gotoS(cc, t, g)));
                    atsparse.get(cc).get(t).add(ss);
                } else if (lrp.equals(acclrp)) {
                    atsparse.get(cc).get(new EOF()).add(new Accept());
                } else if (!lrp.isAnyAfterCursor()) {
                    Production tempp = lrp.prodWithoutCursor();
                    Reduce r = new Reduce(nonTermMap.get(tempp.lhs),
                            tempp.rhs.size());
                    atsparse.get(cc).get(lrp.look).add(r);
                }
            }
        }

        // Initialize the goto table
        for (HashSet<LRPair> cc : ccSet)
            for (NonTerminal nont : safents) {
                gtsparse.put(cc, new HashMap<NonTerminal, HashSet<LRPair>>());
            }

        // Fill the goto table
        for (NonTerminal nont : safents) {
            for (HashSet<LRPair> cc : ccSet) {
                gtsparse.get(cc).put(nont, gotoS(cc, nont, g));
            }
        }

        // fill the packed array versions
        int[] packedATSizes = new int[2];
        packedATSizes[0] = ccMap.size();
        packedATSizes[1] = termMap.size();
        Class atclasstype = (new HashSet<LRPair>()).getClass();
        at = (HashSet<LRAction>[][]) Array.newInstance(atclasstype,
                packedATSizes);
        gt = new int[ccMap.size()][nonTermMap.size()];
        for (HashSet<LRPair> cc : ccSet) {
            for (Terminal term : termMap.keySet()) {
                at[ccMap.get(cc)][termMap.get(term)] = atsparse.get(cc).get(
                        term);
            }
            for (NonTerminal nont : safents) {
                if (gtsparse.get(cc).get(nont).isEmpty()) {
                    gt[ccMap.get(cc)][nonTermMap.get(nont)] = -1;
                } else {
                    gt[ccMap.get(cc)][nonTermMap.get(nont)] = ccMap
                            .get(gtsparse.get(cc).get(nont));
                }
            }
        }
    }

    static HashSet<HashSet<LRPair>> mkCC(CFG g) {
        LinkedList<HashSet<LRPair>> work = new LinkedList<HashSet<LRPair>>();
        HashSet<HashSet<LRPair>> ret = new HashSet<HashSet<LRPair>>();
        boolean changed = true;
        Production goalprod = new Production(g.start, new ArrayList<Symbol>());
        HashSet<LRPair> goallrps = new HashSet<LRPair>();
        HashSet<LRPair> temp;
        for (Production p : g.prodsOf(g.start))
            goalprod = p.clone();
        goalprod.rhs.add(0, new Cursor());
        goallrps.add(new LRPair(goalprod, new EOF()));
        work.offer(closure(goallrps, g));
        ret.add(closure(goallrps, g));
        while (changed) {
            changed = false;
            while (!work.isEmpty()) {
                HashSet<LRPair> currentCC = work.remove();
                for (LRPair lrp : currentCC) {
                    if (lrp.isAnyAfterCursor()) {
                        temp = gotoS(currentCC, lrp.getAfterCursor(), g);
                        if (ret.add(temp))
                            work.offer(temp);
                    }
                }
            }
        }
        return ret;
    }

    // Note this should have the cursor exactly once in each pair
    static HashSet<LRPair> closure(HashSet<LRPair> s, CFG g) {
        HashSet<LRPair> ret = DeepCopy.copy(s);
        HashSet<LRPair> old = null;
        HashSet<LRPair> temp;
        while (!ret.equals(old)) {
            old = DeepCopy.copy(ret);
            temp = DeepCopy.copy(ret);
            for (LRPair lrp : ret) {
                if (lrp.isNTAfterCursor()) {
                    for (Production p : g.prodsOf((NonTerminal) lrp
                            .getAfterCursor())) {
                        Production cprod = DeepCopy.copy(p);
                        cprod.rhs.add(0, new Cursor());
                        if (lrp.isTwoAfterCursor()) {
                            int cursor = lrp.indexOfCursor();
                            for (Terminal t : g.first(lrp.prod.rhs
                                    .get(cursor + 2))) {
                                temp.add(new LRPair(cprod, t));
                            }
                        } else {
                            temp.add(new LRPair(cprod, lrp.look));
                        }
                    }
                }
            }
            ret = temp;
        }
        return ret;
    }

    private static HashSet<LRPair> gotoS(HashSet<LRPair> s, Symbol t, CFG g) {
        HashSet<LRPair> moved = new HashSet<LRPair>();
        LRPair nlrp;
        int cursor;
        for (LRPair i : s) {
            if (i.isAfterCursor(t)) {
                nlrp = new LRPair(i);
                cursor = nlrp.indexOfCursor();
                nlrp.prod.rhs.remove(cursor);
                nlrp.prod.rhs.add(cursor + 1, new Cursor());
                moved.add(nlrp);
            }
        }
        return closure(moved, g);
    }
}
