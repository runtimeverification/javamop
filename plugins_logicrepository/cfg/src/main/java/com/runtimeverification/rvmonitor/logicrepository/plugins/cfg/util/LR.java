package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.util.*;
import java.lang.reflect.Array;

// NB this does not generate LR tables in their full generality merely for epsilon free CFGs
public class LR implements java.io.Serializable {
    private HashSet<LRAction>[][] at;
    private int[][] gt;
    private HashMap<NonTerminal,Integer> nonTermMap = new HashMap<NonTerminal,Integer>();
    private int start;
    private int q0 = 0;
    
    public LR(LR old) {
        this(old.at,old.gt,old.start,old.q0);
    }
    public LR(HashSet<LRAction>[][] atold, int[][] gtold, int startold, int qold) {
        at = DeepCopy.copy(atold); 
        gt = DeepCopy.copy(gtold); 
        start = startold; 
        q0=qold;
    }
    
    @Override
    public int hashCode() { 
        return Arrays.deepHashCode(at) + Arrays.deepHashCode(gt);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof LR)) {
            return false;
        }
        return at.equals(((LR)o).at) && gt.equals(((LR)o).gt) && start == (((LR)o).start) && q0 == (((LR)o).q0);
    }
    
    @Override
    public String toString() {
        String ret = "Start: "+start+", "+q0;
        ret+="\nNonTermMap: \n"+nonTermMap.toString();
        ret+="\nAction table:\n";
        for (int i = 0; i < at.length; i++) {
            ret += Integer.toString(i);
            for (int j = 0; j < at[0].length; j++) {
                ret += " "+at[i][j];
            }
            ret += "\n";
        }
        
        ret += "\nGoto table:\n";
        for (int i = 0; i < gt.length; i++) {
            ret += Integer.toString(i);
            for (int j = 0; j < gt[0].length; j++) {
                ret += " "+gt[i][j];
            }
            ret += "\n";
        }
        return ret;
    }
    
    // The layout of the ActionTable is as follows all mapped to integers
    // [State][Terminal][ActionSet]
    //
    // The layout of a given ActionSet is [LRAction] where Shift k |-> [k], Reduce x y |-> [x,2*y], Accept |-> []
    public int[][][][] atArray() {
        int[][][][] ret = new int[at.length][at[0].length][][];
        for (int i = 0; i < at.length; i++) {
            for (int j = 1; j < at[0].length; j++) {
                ret[i][j] = new int[at[i][j].size()][];
                int k = 0;
                for (LRAction a : at[i][j]) {
                    switch (a.type()) {
                        case SHIFT:
                            ret[i][j][k] = new int[1];
                            ret[i][j][k][0] = ((Shift)a).getTarget();
                            break;
                        case REDUCE:
                            ret[i][j][k] = new int[2];
                            ret[i][j][k][0] = ((Reduce)a).getNt();
                            ret[i][j][k][1] = 2*((Reduce)a).getSize();
                            break;
                        case ACCEPT:
                            ret[i][j][k] = new int[0];
                            break;
                    }
                    k++;
                }
            }
            // We can never try to shift an EOF and we accept if we reduce
            // Thus we only need to know if we do anything on an EOF
            if (at[i][0].isEmpty()) ret[i][0] = new int[0][];
            else ret[i][0] = new int[1][];
        }
        return ret;
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
        ret += " };";
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
        ret += " };";
        return ret;
    }
    
    /**
     * Construct a LR parser.
     * @param g The grammar the parser should parse.
     * @param termMap A mapping from terminals to indexes.
     */
    public LR(CFG g, HashMap<Terminal,Integer> termMap) {
        // CT p128
        HashMap<HashSet<LRPair>,HashMap<Terminal,HashSet<LRAction>>> atsparse = new HashMap<HashSet<LRPair>,HashMap<Terminal,HashSet<LRAction>>>();
        HashMap<HashSet<LRPair>,HashMap<NonTerminal,HashSet<LRPair>>> gtsparse = new HashMap<HashSet<LRPair>,HashMap<NonTerminal,HashSet<LRPair>>>();
        HashMap<HashSet<LRPair>,Integer> ccMap = new HashMap<HashSet<LRPair>,Integer>();
        HashSet<HashSet<LRPair>> ccSet = mkCC(g);
        Terminal t;
        NonTerminal nt;
        HashSet<NonTerminal> safents = g.nonTerminals();
        safents.remove(g.getStart());
        
        // Populate the integer maps
        // NB -1 indicates errors
        int hashint = 0;
        for (HashSet<LRPair> cc : ccSet) {
            ccMap.put(cc,hashint++);
        }
        hashint = 0;
        for (NonTerminal nont : g.nonTerminals()) {
            nonTermMap.put(nont,hashint++);
        }
        termMap.put(new EOF(),0);
        
        // Calculate the goal production
        Production goalprod = new Production(g.getStart(),new ArrayList<Symbol>());
        HashSet<LRPair> goallrps = new HashSet<LRPair>();
        HashSet<LRPair> temp;
        for (Production p : g.prodsOf(g.getStart())) {
            goalprod = p.clone();
        }
        goalprod.getRhs().add(0,new Cursor());
        goallrps.add(new LRPair(goalprod,new EOF()));
        
        q0 = ccMap.get(closure(goallrps,g));
        
        // Initialze the action table to be empty
        for (HashSet<LRPair> cc : ccSet) {
            atsparse.put(cc,new HashMap<Terminal,HashSet<LRAction>>());
            for (Terminal terminal : termMap.keySet()) {
                atsparse.get(cc).put(terminal,new HashSet<LRAction>());
            }
        }
        
        // the production to accept on
        Production accprod = null;
        for (Production p : g.prodsOf(g.getStart())) {
            accprod = p.clone();
        }
        accprod.getRhs().add(new Cursor());
        LRPair acclrp = new LRPair(accprod,new EOF());
        
        // Fill the action table
        for (HashSet<LRPair> cc : ccSet) {
            for (LRPair lrp : cc) {
                if (lrp.isTAfterCursor()) {
                    t = (Terminal)lrp.getAfterCursor();
                    Shift ss = new Shift(ccMap.get(gotoS(cc,t,g)));
                    atsparse.get(cc).get(t).add(ss);
                }
                else if (lrp.equals(acclrp)) {
                    atsparse.get(cc).get(new EOF()).add(new Accept());
                }
                else if (!lrp.isAnyAfterCursor()) {
                    Production tempp = lrp.prodWithoutCursor();
                    Reduce r = new Reduce(nonTermMap.get(tempp.getLhs()),tempp.getRhs().size());
                    atsparse.get(cc).get(lrp.getLook()).add(r);
                }
            }
        }
        
        // Initialize the goto table
        for (HashSet<LRPair> cc : ccSet)
            for (NonTerminal nont: safents) {
                gtsparse.put(cc,new HashMap<NonTerminal,HashSet<LRPair>>());
            }
            
            // Fill the goto table
            for (NonTerminal nont : safents) {
                for (HashSet<LRPair> cc : ccSet){
                    gtsparse.get(cc).put(nont,gotoS(cc,nont,g));
                }
            }
            
            // fill the packed array versions
            int[] packedATSizes = new int[2]; packedATSizes[0] = ccMap.size(); packedATSizes[1] = termMap.size();
            Class atclasstype = (new HashSet<LRPair>()).getClass();
            at = (HashSet<LRAction>[][]) Array.newInstance(atclasstype,packedATSizes);
            gt = new int[ccMap.size()][nonTermMap.size()];
            for (HashSet<LRPair> cc : ccSet) {
                for (Terminal term : termMap.keySet()) {
                    at[ccMap.get(cc)][termMap.get(term)] = atsparse.get(cc).get(term);
                }
                for (NonTerminal nont : safents) {
                    if (gtsparse.get(cc).get(nont).isEmpty()) {
                        gt[ccMap.get(cc)][nonTermMap.get(nont)] = -1;
                    } else {
                        gt[ccMap.get(cc)][nonTermMap.get(nont)] = ccMap.get(gtsparse.get(cc).get(nont));
                    }
                }
            }
    }
    
    private static HashSet<HashSet<LRPair>> mkCC(CFG g) {
        LinkedList<HashSet<LRPair>> work = new LinkedList<HashSet<LRPair>>();
        HashSet<HashSet<LRPair>> ret = new HashSet<HashSet<LRPair>>();
        boolean changed = true;
        Production goalprod = new Production(g.getStart(),new ArrayList<Symbol>());
        HashSet<LRPair> goallrps = new HashSet<LRPair>();
        HashSet<LRPair> temp;
        for (Production p : g.prodsOf(g.getStart())) {
            goalprod = p.clone();
        }
        goalprod.getRhs().add(0,new Cursor());
        goallrps.add(new LRPair(goalprod,new EOF()));
        work.offer(closure(goallrps,g));
        ret.add(closure(goallrps,g));
        while (changed) {
            changed = false;
            while (!work.isEmpty()) {
                HashSet<LRPair> currentCC = work.remove();
                for (LRPair lrp : currentCC) {
                    if (lrp.isAnyAfterCursor()) {
                        temp = gotoS(currentCC,lrp.getAfterCursor(),g);
                        if (ret.add(temp)) work.offer(temp);
                    }
                }
            }
        }
        return ret;
    }
    
    // Note this should have the cursor exactly once in each pair
    private static HashSet<LRPair> closure(HashSet<LRPair> s, CFG g) {
        HashSet<LRPair> ret = DeepCopy.copy(s);
        HashSet<LRPair> old = null;
        HashSet<LRPair> temp;
        while (!ret.equals(old)) {
            old = DeepCopy.copy(ret);
            temp = DeepCopy.copy(ret);
            for (LRPair lrp : ret) {
                if (lrp.isNTAfterCursor()) {
                    for (Production p : g.prodsOf((NonTerminal)lrp.getAfterCursor())) {
                        Production cprod = DeepCopy.copy(p);
                        cprod.getRhs().add(0,new Cursor());
                        if (lrp.isTwoAfterCursor()) {
                            int cursor = lrp.indexOfCursor();
                            for (Terminal t : g.first(lrp.getProd().getRhs().get(cursor+2))) {
                                temp.add(new LRPair(cprod,t));
                            }
                        } else {
                            temp.add(new LRPair(cprod,lrp.getLook()));
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
                nlrp.getProd().getRhs().remove(cursor);
                nlrp.getProd().getRhs().add(cursor+1,new Cursor());
                moved.add(nlrp);
            }
        }
        return closure(moved,g);
    }
    
    public int getStart() {
        return start;
    }
    
    public int getQ0() {
        return q0;
    }
    
    public int[][] getGt() {
        return gt;
    }
}
