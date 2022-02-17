package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * A context-free grammar, stored as a start symbol and set of productions.
 */
public class CFG implements java.io.Serializable {
    private HashSet<Production> prods;
    private NonTerminal start;
    
    private HashSet<Terminal> ts = null;
    private HashSet<NonTerminal> nts = null;
    
    // WARNING the following cached queries should not be used after modification
    private HashMap<Symbol,HashSet<Terminal>> firstSet = null;
    private HashMap<Symbol,HashSet<Terminal>> followsSet = null;
    
    /**
     * Construct an empty context-free grammar.
     */
    public CFG() {
        prods = new HashSet<Production>();
    }
    
    /**
     * Invalidate the cached results as they are out of date from the grammar being modified.
     */
    public void invalidate() {
        ts = null;
        nts = null;
        firstSet = null;
        followsSet = null;
    }
    
    /**
     * Add a production to the grammar.
     * @param p The production to add.
     */
    public void add(Production p) {
        prods.add(p);
        invalidate();
    }
    
    /**
     * Add several productions to the grammar.
     * @param ps A collection of Productions to add.
     */
    public void add(Collection<Production> ps) {
        for (Production p : ps) {
            add(p);
        }
        invalidate();
    }
    
    /**
     * The events which should trigger the creation of a monitor based on this FSM.
     * @return A string with the creation events.
     */
    public String creationEvents() {
        // Note these are only for match
        HashMap<Terminal,HashSet<HashSet<Terminal>>> emap = enableSets();
        String ret = "";
        for (Terminal t : emap.keySet()) {
            if (emap.get(t).contains(new HashSet<Terminal>())) {
                ret+= t.toString();
            }
        }
        return ret;
    }
    
    /**
     * Return the string of all enable events.
     * @return A string with the enables events for exporting.
     */
    public String enablesString() {
        // Note these are only for match
        return "// match Enables\n" + preenableSets().toString();
    }
    
    /**
     * Produce the enable sets for when the program should react to the grammar failing to parse.
     * @return The enable set as a string.
     */
    public String failenables() {
        HashMap<Terminal,HashSet<HashSet<Terminal>>> emap = 
            new HashMap<Terminal,HashSet<HashSet<Terminal>>>();
        HashSet<HashSet<Terminal>> pow = Util.powerSet(terminals());
        for (Terminal t : terminals()) {
            emap.put(t,pow);
        }
        return "// fail Enables\n"+emap.toString();
    }
    
    @Override
    public String toString() {
        // I wish java had typeclass deriving
        // This is meant for debuging, thus there is no effor to collapse alts
        String s = "Start: "+start.toString()+"\n";
        for (Production p : prods) {
            s += p.toString() + "\n";
        }
        return s;
    }
    
    
    /**
     * Calculate the first set for all symbols in the grammar.
     * @return A mapping from a symbol to its first set.
     */
    private HashMap<Symbol,HashSet<Terminal>> firstS() {
        // See Cooper and Torczon p 99
        // I am not quite sure what will happen if there are epsilons left in the grammar
        // before running this
        // Either way RVM doesn't need epsilons so we will use this latter
        HashMap<Symbol,HashSet<Terminal>> ret = new HashMap<Symbol,HashSet<Terminal>>();
        HashSet<Symbol> temp = new HashSet<Symbol>();
        boolean changed = true;
        for (Terminal t : terminals()) {
            temp.add(t);
            ret.put(t,(HashSet<Terminal>)temp.clone());
            temp.clear();
        }
        for (NonTerminal nt : nonTerminals()) {
            ret.put(nt,new HashSet<Terminal>());
        }
        while (changed) {
            changed = false;
            for (Production p : prods) {
                if (!ret.get(p.getLhs()).containsAll(ret.get(p.getRhs().get(0)))) {
                    ret.get(p.getLhs()).addAll(ret.get(p.getRhs().get(0)));
                    changed = true;
                }
            }
        }
        return ret;
    }
    
    /**
     * The first set of a particular symbol.
     * @param t The symbol to find the first set of.
     * @return The first set of {@code t}.
     */
    public HashSet<Terminal> first(Symbol t) { 
        if (firstSet == null) {
            firstSet = firstS(); 
        }
        return firstSet.get(t); 
    }
    
    /**
     * The first set of the first symbol in the given list.
     * @param l The list to take the first Symbol from.
     * @return The first set of the first symbol in {@code l}.
     */
    private HashSet<Terminal> first(ArrayList<Symbol> l) { 
        if (l.isEmpty()) {
            return new HashSet<Terminal>();
        }
        return first(l.get(0));
    }
    
    /**
     * Calculate the follow set for all symbols.
     * @return A mapping from a symbol to its follow set.
     */
    private HashMap<Symbol,HashSet<Terminal>> followsS() {
        // CT p99 only finds follows sets for nonterminals, but there should be no danger in finding them for terminals too
        HashMap<Symbol,HashSet<Terminal>> ret = new HashMap<Symbol,HashSet<Terminal>>();
        boolean changed = true;
        for (Terminal t : terminals())
            ret.put(t,new HashSet<Terminal>());
        for (NonTerminal nt : nonTerminals())
            ret.put(nt,new HashSet<Terminal>());
        ret.get(start).add(new EOF());
        while (changed) {
            changed = false;
            for (Production p : prods) {
                ArrayList<Symbol> rhs = p.getRhs();
                changed = ret.get(rhs.get(rhs.size()-1)).addAll(ret.get(p.getLhs())) || changed;
                if (rhs.size() > 1) {
                    for (int i = rhs.size(); i > 0; i--) {
                        changed = ret.get(rhs.get(i-1)).addAll(first(rhs.get(i-1))) || changed;
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * The follow set for a given symbol.
     * @param s The symbol to get the follow set for.
     */
    private HashSet<Terminal> follows(Symbol s) { 
        if (followsSet == null) {
            followsSet = followsS(); 
        }
        return followsSet.get(s); 
    }
    
    /**
     * All the non-terminals in the grammar. This caches its result, so don't modify the grammar
     * after invoking.
     * @return A set of all the nonterminals in the grammar.
     */
    public HashSet<NonTerminal> nonTerminals() {
        // What are the nonterminals in this grammar
        if(nts != null) {
            return nts;
        }
        nts = new HashSet<NonTerminal>();
        for (Production p : prods) {
            nts.add(p.getLhs());
        }
        return nts;
    }
    
    /**
     * All the terminals in the grammar. This caches its result, so don't modify the grammar after
     * invoking.
     * @return A set of all the terminals in the grammar.
     */
    public HashSet<Terminal> terminals() {
        if (ts != null) {
            return ts;
        }
        ts = new HashSet<Terminal>();
        for (Production p : prods) {
            for (Symbol s : p.getRhs()) {
                if (s instanceof Terminal) {
                    ts.add((Terminal)s);
                }
            }
        }
        return ts;
    }
    
    /**
     * Simplify the internal grammar.
     */
    public void simplify() {
        // The simplification conists the following steps:
        // 1) Remove nongenerating productions
        // 2) Remove nonreachable productions
        // 3) Inline/remove epsilons
        // 4) Remove A -> A productions
        // 5) Merge nonterminals that differ only in their names
        // 6) Add a new start symbol, this is mostly for LR table generation
        removeNonGenerating();
        removeNonReachable();
        removeEpsilons();
        removeSelfLoops();
        removeDupes();
        newStart();
    }
    
    /**
     * Change the start symbol into a new symbol that expands into the old symbol.
     * Makes generating a LR parser easier as the new start symbol is guaranteed to have only
     * one expansion and nothing will expand into it.
     */
    public void newStart() {
        NonTerminal nt = freshNT();
        ArrayList<Symbol> rhs = new ArrayList<Symbol>();
        rhs.add(start);
        prods.add(new Production(nt,rhs));
        start = nt;
    }
    
    /**
     * Change the start symbol into a new symbol that expands into the old symbol if there are
     * any expansions that produce the start symbol. Makes generating a LR parser easier as the 
     * new start symbol is guaranteed to have only one expansion and nothing will expand into it.
     */
    public void removeStartCycles() {
        if (!prodsMentioning(start).isEmpty()) {
            NonTerminal nt = freshNT();
            ArrayList<Symbol> rhs = new ArrayList<Symbol>();
            rhs.add(start);
            prods.add(new Production(nt,rhs));
            start = nt;
        }
    }
    
    private int freshnamenum = 0;
    
    /**
     * Construct a NonTerminal with a name that isn't already in the grammar.
     * @return A unique new unused NonTerminal.
     */
    public NonTerminal freshNT() {
        HashSet<NonTerminal> nts = nonTerminals();
        while (nts.contains(new NonTerminal(Integer.toString(freshnamenum)))) {
            freshnamenum++;
        }
        return new NonTerminal(Integer.toString(freshnamenum));
    }
    
    /**
     * Remove duplicate productions internally.
     */
    public void removeDupes() {
        HashMap<HashSet<ArrayList<Symbol>>,NonTerminal> x = 
            new HashMap<HashSet<ArrayList<Symbol>>,NonTerminal>();
        boolean changed = true;
        while (changed) {
            changed = false;
            x.clear();
            x.put(rhssOf(start),start);
            for (NonTerminal nt : nonTerminals()) {
                HashSet<ArrayList<Symbol>> rhss = rhssOf(nt);
                if (!x.containsKey(rhss)) {
                    x.put(rhss,nt);
                } else {
                    replaceRHSNTs(x.get(rhss),nt);
                }
            }
            changed = cropToNTs(x.values());
        }
    }
    
    /**
     * A set of all the right hand sides / expansions of the given NonTerminal.
     * @param nt The nonterminal to get the expansions for.
     * @return A set of all the production results of {@code nt}.
     */
    private HashSet<ArrayList<Symbol>> rhssOf(NonTerminal nt) {
        HashSet<Production> ps = prodsOf(nt);
        HashSet<ArrayList<Symbol>> ret = new HashSet<ArrayList<Symbol>>();
        for (Production p : ps) {
            ret.add(p.getRhs());
        }
        return ret;
    }
    
    /**
     * Replace all instances of {@code o} with {@code n} in the grammar.
     * @param n The nonterminal to insert.
     * @param o The nonterminal to replace.
     */
    private void replaceRHSNTs(NonTerminal n, NonTerminal o) {
        for (Production p : prods) {
            p.replaceRHSNTs(n,o);
        }
    }
    
    /**
     * Rewrite the grammar to remove the epsilon expansions.
     */
    public void removeEpsilons() {
        HashSet<Production> eps = epsilons();
        LinkedList<Production> work = new LinkedList<Production>();
        HashSet<Production> nps = new HashSet<Production>();
        Production p, ep, np;
        NonTerminal nt;
        while (hasEpsilon()) {
            ep = getEpsilon(); nt = ep.getLhs();
            work.addAll(prodsMentioning(nt));
            while (!work.isEmpty()) {
                p = work.remove();
                for (int i = 0; i < p.getRhs().size(); i++) {
                    if (p.getRhs().get(i).equals(nt) && p.getRhs().size() > 1) {
                        np = new Production(p);
                        np.getRhs().remove(i);
                        nps.add(np);
                        work.add(np);
                    }
                }
            }
            prods.remove(ep);
            prods.addAll(nps);
        }
    }
    
    /**
     * Retrieve a production that expands into an epsilon.
     * @return A production with an epsilon expansion, or {@code null} if none exists.
     */
    private Production getEpsilon() {
        // Return an epsilon production nondeterministically
        // Explodes if there are no epsilons
        for(Production p : epsilons()) {
            return p;
        }
        return null;
    }
    
    /**
     * Whether there exists a production that expands into an epsilon.
     * @return If there is an espilon production.
     */
    private boolean hasEpsilon() {
        return !epsilons().isEmpty();
    }
    
    /**
     * Find all productions that contain the given NonTerminal in their expansions.
     * @param nt The nonterminal to search for.
     * @return The set of all the productions containing {@code nt}.
     */
    private HashSet<Production> prodsMentioning(NonTerminal nt) {
        HashSet<Production> ret = new HashSet<Production>();
        for (Production p : prods) {
            if (p.contains(nt)) {
                ret.add(p);
            }
        }
        return ret;
    }
    
    /**
     * Find all productions that contain the given Terminal in their expansions.
     * @param t The terminal to search for.
     * @return The set of all the productions containing {@code t}.
     */
    private HashSet<Production> prodsMentioning(Terminal t) {
        HashSet<Production> ret = new HashSet<Production>();
        for (Production p : prods) {
            if (p.contains(t)) {
                ret.add(p);
            }
        }
        return ret;
    }
    
    /**
     * The right hand side of all productions.
     * @return The set of all right hand sides of all productions.
     */
    private HashSet<ArrayList<Symbol>> bodies() {
        HashSet<ArrayList<Symbol>> ret = new HashSet<ArrayList<Symbol>>();
        for (Production p : prods) {
            ret.add(p.getRhs());
        }
        return ret;
    }
    
    /**
     * All the sets of sets of terminals that can be produced by all fragments of expansions.
     * @return A mapping from partial expansions to sets of sets of terminals.
     */
    private HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>> genSets() {
        HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>> ret = 
            new HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>>();
        HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>> old = 
            new HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>>();
        
        // Initialize all prefixes of productions as well as symbols
        // Note that G({})={{}} not merely {}
        for (ArrayList<Symbol> rhs : bodies()) {
            for (ArrayList<Symbol> init : Util.inits(rhs)) {
                ret.put(init,new HashSet<HashSet<Terminal>>());
            }
        }
        for (Symbol t : terminals()) {
            ret.put(Util.singletonAL(t),new HashSet<HashSet<Terminal>>());
        }
        for (Symbol t : nonTerminals()) {
            ret.put(Util.singletonAL(t),new HashSet<HashSet<Terminal>>());
        }
        ret.put(new ArrayList<Symbol>(),Util.singletonHS(new HashSet<Terminal>()));
        
        // Terminals only generate themselves
        // Also these don't depend on any other value so don't
        // need to be in the fixed point section
        // G(t)={{t}}
        for (Terminal t : terminals()) {
            ret.put(Util.singletonAL((Symbol)t),Util.singletonHS(Util.singletonHS(t)));
        }
        
        // For Nonterminals we need to find a fixed point
        // G(V) = { G(...) : V -> ... }
        // G(D_1D_2) = { x U y : x \in G(D_1), y \in G(D_2) }
        while (!ret.equals(old)) {
            old = DeepCopy.copy(ret);
            for (Symbol nt : nonTerminals()) {
                for (Production p : prodsOf((NonTerminal)nt)) {
                    ret.get(Util.singletonAL(nt)).addAll(ret.get(p.getRhs()));
                }
            }
            for (ArrayList<Symbol> rhs : bodies()) {
                for (ArrayList<Symbol> init : Util.neinits(rhs)) {
                    ArrayList<Symbol> temp = (ArrayList<Symbol>)init.clone();
                    for (HashSet<Terminal> x : ret.get(Util.popl(temp))) {
                        for (HashSet<Terminal> y: ret.get(temp)) {
                            HashSet<Terminal> temp2 = DeepCopy.copy(x);
                            temp2.addAll(y);
                            ret.get(init).add(temp2);
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * First pass at calculating the enable sets for the grammar.
     * @return The first pass of the enable sets calculation.
     */
    private HashMap<Terminal,HashSet<HashSet<Terminal>>> preenableSets() {
        // Note this only finds the enable sets when ACCEPT is the goal category
        // I have no idea what to do for FAIL
        HashMap<Symbol,HashSet<HashSet<Terminal>>> preenableSetsS = preenableSetsS();
        HashMap<Terminal,HashSet<HashSet<Terminal>>> ret = 
            new HashMap<Terminal,HashSet<HashSet<Terminal>>>();
        for (Terminal t : terminals()) {
            ret.put(t,preenableSetsS.get(t));
        }
        return ret;
    }
    
    /**
     * First pass at calculating the enable sets for the grammar, expressed with all symbols.
     * @return The first pass of the enable sets calculation for all symbols.
     */
    private HashMap<Symbol,HashSet<HashSet<Terminal>>> preenableSetsS() {
        HashMap<ArrayList<Symbol>,HashSet<HashSet<Terminal>>> genSets = genSets();
        HashMap<Symbol,HashSet<HashSet<Terminal>>> ret = 
            new HashMap<Symbol,HashSet<HashSet<Terminal>>>();
        HashMap<Symbol,HashSet<HashSet<Terminal>>> old = null;
        HashMap<Symbol,HashSet<HashSet<Terminal>>> temp = null;
        
        // Initialize the maps to be empty
        // The start symbol has nothing infront of it
        for (Terminal t : terminals()) {
            ret.put(t,new HashSet<HashSet<Terminal>>());
        }
        for (NonTerminal nt : nonTerminals()) {
            ret.put(nt,new HashSet<HashSet<Terminal>>());
        }
        ret.put(start,Util.singletonHS(new HashSet<Terminal>()));
        
        while (!ret.equals(old)) {
            old = DeepCopy.copy(ret);
            temp = DeepCopy.copy(ret);
            for (Terminal t : terminals()) {
                for (Production p : prodsMentioning(t)) {
                    for (HashSet<Terminal> x : ret.get(p.getLhs())) {
                        for (ArrayList<Symbol> rhs : p.beforeSymS(t)) {
                            for (HashSet<Terminal> y : genSets.get(rhs)) {
                                temp.get(t).add(Util.termUnion(x,y));
                            }
                        }
                    }
                }
            }
            for (NonTerminal nt : nonTerminals()) {
                for (Production p : prodsMentioning(nt)) {
                    for (HashSet<Terminal> x : ret.get(p.getLhs())) {
                        for (ArrayList<Symbol> rhs : p.beforeSymS(nt)) {
                            for (HashSet<Terminal> y : genSets.get(rhs)) {
                                temp.get(nt).add(Util.termUnion(x,y));
                            }
                        }
                    }
                }
            }
            ret = temp;
        }
        return ret;
    }
    
    /**
     * Reverse the grammar internally.
     */
    private void reverse() {
        // We just need to reverse each production
        for (Production p : prods) {
            for (int i = 0; i < p.getRhs().size()/2; i++) {
                Symbol temp = p.getRhs().get(i);
                p.getRhs().set(i,p.getRhs().get(p.getRhs().size()-(i+1)));
                p.getRhs().set(p.getRhs().size()-(i+1),temp);
            }
        }
    }
    
    /**
     * First pass at the coenable sets for all terminals.
     * @return A first pass at calculating the coenable sets.
     */
    private HashMap<Terminal,HashSet<HashSet<Terminal>>> copreenableSets() {
        // Like for enable sets this only works for match as the goal category
        reverse();
        HashMap<Terminal,HashSet<HashSet<Terminal>>> ret = preenableSets();
        reverse();
        return ret;
    }
    
    /**
     * The enable sets for all terminals.
     * @return A mapping from terminals to sets of sets of Terminals describing monitors to start.
     */
    private HashMap<Terminal,HashSet<HashSet<Terminal>>> enableSets() {
        HashMap<Terminal,HashSet<HashSet<Terminal>>> ret = 
            new HashMap<Terminal,HashSet<HashSet<Terminal>>>();
        HashMap<Terminal,HashSet<HashSet<Terminal>>> preenableSets = preenableSets();
        for (Terminal t : terminals()) {
            HashSet<HashSet<Terminal>> temp = new HashSet<HashSet<Terminal>>();
            for (HashSet<Terminal> set : preenableSets.get(t))
                if (!set.contains(t)) temp.add(set);
                ret.put(t,temp);
        }
        return ret;
    }
    
    /**
     * The coenable sets for all terminals.
     * @return A mapping from terminals to sets of sets of Terminals describing monitors to start.
     */
    private HashMap<Terminal,HashSet<HashSet<Terminal>>> coenableSets() {
        HashMap<Terminal,HashSet<HashSet<Terminal>>> ret = copreenableSets();
        for (Terminal t : terminals())
            ret.get(t).remove(new HashSet<Terminal>());
        return ret;
    }
    
    /**
     * Remove the nonterminals that have no productions.
     */
    public void removeNonGenerating() {
        // This looks to be quadratic and probably doesn't need to be
        HashSet<NonTerminal> gens = new HashSet<NonTerminal>();
        boolean changed = true;
        while (changed) {
            changed = false;
            HashSet<Symbol> temp = Util.union(gens,terminals());
            for (Production p : prods) {
                if (temp.containsAll(p.getRhs()) || p.isEpsilon()) {
                    changed = gens.add(p.getLhs()) || changed;
                }
            }
        }
        cropToNTs(gens);
    }
    
    /**
     * Crop the productions to those of the given nonterminals.
     * @param nts The nonterminals to keep productions for.
     * @return {@code true} if any productions were removed, {@code false} if not.
     */
    public boolean cropToNTs(Collection<NonTerminal> nts) {
        HashSet<Production> t = new HashSet<Production>();
        boolean ret;
        for (Production p : prods) {
            if (nts.contains(p.getLhs())) {
                t.add(p);
            }
        }
        ret = prods.size() > t.size();
        prods = t;
        return ret;
    }
    
    /**
     * Remove unreachable productions from the internal production list.
     */
    public void removeNonReachable() {
        LinkedList<NonTerminal> work = new LinkedList<NonTerminal>();
        HashSet<NonTerminal> r = new HashSet<NonTerminal>();
        HashSet<NonTerminal> nts;
        work.add(start); r.add(start);
        while (!work.isEmpty()) {
            for (Production p : prodsOf(work.remove())) {
                for (NonTerminal nt : p.nonTerminals()) {
                    if (!r.contains(nt)) {
                        r.add(nt); 
                        work.add(nt);
                    }
                }
            }
        }
        cropToNTs(r);
    }
    
    /**
     * All the productions of a particular nonterminal.
     * @param nt The nonterminal to search for productions for.
     * @return The set of productions of the given nonterminal.
     */
    public HashSet<Production> prodsOf(NonTerminal nt) {
        HashSet<Production> r = new HashSet<Production>();
        for (Production p : prods) {
            if (p.getLhs().equals(nt)) {
                r.add(p);
            }
        }
        return r;
    }
    
    /**
     * Remove all productions with themself as the sole member of the expansion from the 
     * production list.
     */
    public void removeSelfLoops() { 
        // With a name like this I wonder if we should just use a graph
        // library for the manipulations
        prods.removeAll(selfLoops());
    }
    
    /**
     * All productions that can expand to an epsilon or empty string.
     * @return All the productions with epsilon expansions.
     */
    private HashSet<Production> epsilons() {
        HashSet<Production> ret = new HashSet<Production>();
        for (Production p : prods) {
            if (p.isEpsilon()) {
                ret.add(p);
            }
        }
        return ret;
    }
    
    /**
     * All productions that have the nonterminal as the first member of the expansion.
     * @return All the productions with themselves as the first member of the expansion.
     */
    private HashSet<Production> selfLoops() {
        HashSet<Production> ret = new HashSet<Production>();
        for (Production p : prods) {
            if (p.isSelfLoop()) {
                ret.add(p);
            }
        }
        return ret;
    }
    
    /**
     * All the productions.
     * @return A set of all the productions.
     */
    public Set<Production> getProds() {
        return Collections.unmodifiableSet(prods);
    }
    
    /**
     * The starting nonterminal that expands into the complete expression.
     * @return The starting nonterminal.
     */
    public NonTerminal getStart() {
        return start;
    }
    
    /**
     * Set the starting nonterminal.
     * @param newStart The new starting nonterminal.
     */
    public void setStart(NonTerminal newStart) {
        this.start = newStart;
        invalidate();
    }
}
