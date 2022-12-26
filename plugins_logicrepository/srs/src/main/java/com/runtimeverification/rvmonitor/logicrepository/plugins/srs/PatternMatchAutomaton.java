/// TODO : consider symbols that don't appear in the SRS?

package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;

//This uses a slightly modified Aho-Corasick automaton
public class PatternMatchAutomaton extends LinkedHashMap<State, HashMap<Symbol, ActionState>> {
    private State s0 = new State(0); 
    private ArrayList<Set<State>> depthMap = new ArrayList<Set<State>>();
    private HashMap<State, State> fail;
    
    private boolean needsBegin = false;
    private boolean needsEnd = false;
    
    public PatternMatchAutomaton(SRS srs) {
        init(srs);
        mkGotoMachine(srs, srs.getTerminals());    
        addFailureTransitions(srs.getTerminals());
    }
    
    public PatternMatchAutomaton(SRS srs, Symbol[] extraTerminals) {
        init(srs);
        Set<Symbol> et = new HashSet<Symbol>();
        for(Symbol s : extraTerminals) {
            et.add(s);
        }
        Set<Symbol> terminals = new HashSet<Symbol>();
        terminals.addAll(srs.getTerminals());
        terminals.addAll(et);
        mkGotoMachine(srs, terminals);
        addFailureTransitions(terminals);
    }
    
    public PatternMatchAutomaton(SRS srs, Set<Symbol> extraTerminals) {
        init(srs);
        Set<Symbol> terminals = new HashSet<Symbol>();
        terminals.addAll(srs.getTerminals());
        terminals.addAll(extraTerminals);
        mkGotoMachine(srs, terminals);
        addFailureTransitions(terminals);
    }
    
    private void init(SRS srs) {
        Symbol begin = Symbol.get("^");
        Symbol end = Symbol.get("$");
        for(Symbol s : srs.getTerminals()) {
            if(s.equals(begin)) {
                needsBegin = true;
            }
            else if(s.equals(end)) {
                needsEnd = true;
            }
        }
    }
    
    private void mkGotoMachine(SRS srs, Set<Symbol> terminals) {
        State currentState;
        put(s0, new HashMap<Symbol, ActionState>()); 
        Set<State> depthStates = new HashSet<State>();
        depthStates.add(s0);
        depthMap.add(depthStates);
        //compute one path through the tree for each lhs
        for(Rule r : srs) {
            currentState = s0;
            Sequence pattern = r.getLhs();
            int patternRemaining = pattern.size() - 1;
            for(Symbol s : pattern) {
                HashMap<Symbol, ActionState> transition = get(currentState);
                ActionState nextActionState = transition.get(s);  
                State nextState;
                if(nextActionState == null) {
                    int nextDepth = currentState.getDepth() + 1;
                    nextState = 
                    new State(nextDepth); 
                    nextActionState = new ActionState(0, nextState);
                    transition.put(s, nextActionState);
                    put(nextState, new HashMap<Symbol, ActionState>());
                    if(nextDepth == depthMap.size()) {
                        depthStates = new HashSet<State>();
                        depthMap.add(depthStates);
                    }
                    else{
                        depthStates = depthMap.get(nextDepth);
                    }
                    depthStates.add(nextState);
                }
                else{
                    nextState = nextActionState.getState();
                }
                if(patternRemaining == 0) {
                    nextState.setMatch(r);
                }
                currentState = nextState;
                --patternRemaining;
            }
        }
        //now add self transitions on s0 for any symbols that don't
        //exit from s0 already
        HashMap<Symbol, ActionState> s0transition = get(s0);
        for(Symbol s : terminals) {
            if(!s0transition.containsKey(s)) {
                s0transition.put(s, new ActionState(0, s0));
            }
        }
    }
    
    private void addFailureTransitions(Set<Symbol> terminals) {
        fail = new HashMap<State, State>();
        if(depthMap.size() == 1) return;
        //handle all depth 1
        for(State state : depthMap.get(1)) {
            HashMap<Symbol, ActionState> transition = get(state);
            fail.put(state, s0);
            for(Symbol symbol : terminals) {
                if(!transition.containsKey(symbol)) {
                    transition.put(symbol, new ActionState(1, s0));
                } 
            } 
        }
        if(depthMap.size() == 2) return;
        //handle depth d > 1
        for(int i = 2; i < depthMap.size(); ++i) {
            for(State state : depthMap.get(i)) {
                HashMap<Symbol, ActionState> transition = get(state);
                for(Symbol symbol : terminals) {
                    if(!transition.containsKey(symbol)) {
                        State failState = findState(state, depthMap.get(i - 1), fail, 
                                                    terminals);
                        transition.put(symbol,  
                                       new ActionState(state.getDepth() - failState.getDepth(), 
                                                       failState));
                        fail.put(state, failState);
                    } 
                } 
            }
        }
        
        //System.out.println("!!!!!!!!!!");
        //System.out.println(fail);
        //System.out.println("!!!!!!!!!!");
    }
    
    private State findState(State state, Set<State> shallowerStates, 
                            HashMap<State, State> fail, Set<Symbol> terminals) {
        for(State shallowerState : shallowerStates) {
            HashMap<Symbol, ActionState> transition = get(shallowerState); 
            for(Symbol symbol : terminals) {
                ActionState destination = transition.get(symbol);
                if(destination.getState() == state) {
                    
                    //  System.out.println(state + " " + destination.getState());
                    State failState = fail.get(shallowerState);
                    while(failState != s0 && get(failState).get(symbol).getAction() != 0) {
                        failState = fail.get(failState);
                    } 
                    return get(failState).get(symbol).getState();
                }
            }
        }
        return s0;
    }
    
    public void rewrite(SinglyLinkedList<Symbol> l) {
        System.out.println("rewriting:");
        //System.out.println("   " + l + "\n=========================================");
        if(l.size() == 0) return;
        Iterator<Symbol> first  = l.iterator();
        first.next(); //make sure first points to an element
        Iterator<Symbol> second = l.iterator();
        Iterator<Symbol> lastRepl;
        State currentState = s0;
        ActionState as;
        Symbol symbol = second.next();
        while(true) {
            as = get(currentState).get(symbol);
            //System.out.println("*" + symbol + " -- " + as);
            //adjust the first pointer
            if(currentState == s0 && as.getState() == s0) {
                //System.out.println("false 0 transition");
                if(!first.hasNext()) break;
                first.next();
            }
            else {
                for(int i = 0; i < as.getAction(); ++i) {
                    first.next();
                }
            }
            if(as.getState().getMatch() != null) {
                AbstractSequence repl = as.getState().getMatch().getRhs();
                if(repl instanceof Fail) {
                    System.out.println("Fail!");
                    return;
                }
                if(repl instanceof Succeed) {
                    System.out.println("Succeed!");
                    return;
                }
                if(repl instanceof Sequence) {
                    //System.out.println("==========Replacing==============" + first);
                    //System.out.println("==========Replacing==============" + second);
                    //System.out.println("in: " + l);
                    l.nonDestructiveReplace(first, second, (Sequence) repl);
                    if(l.isEmpty()) break;
                    first = l.iterator();
                    //System.out.println("out: " + l);
                    //System.out.println("out: " + first);
                    //lastRepl = l.iterator(second);
                    //System.out.println("lastRepl: " + lastRepl);
                    symbol = first.next();
                    second = l.iterator(first);
                    //System.out.println("first: " + first);
                    //System.out.println("second: " + second);
                    currentState = s0;
                    continue;
                }
            }
            if(!second.hasNext()) break;
            currentState = as.getState();
            //normal transition
            if(as.getAction() == 0) {
                symbol = second.next();
            }
            //fail transition, need to reconsider he same symbol in next state
        }
        System.out.println("substituted form = " + l.toString());
    }
    
    public void rewrite(SpliceList<Symbol> l) {
        System.out.println("rewriting:");
        System.out.println("   " + l + "\n=========================================");
        if(l.isEmpty()) return;
        SLIterator<Symbol> first;
        SLIterator<Symbol> second;
        SLIterator<Symbol> lastRepl = null;
        State currentState;
        ActionState as;
        Symbol symbol; 
        boolean changed;
        boolean atOrPastLastChange;
        DONE:
        do {
            currentState = s0;
            atOrPastLastChange = false;
            changed = false;
            first = l.head();
            second = l.head();
            symbol = second.get();
            //System.out.println("******************outer*****");
            while(true) {
                as = get(currentState).get(symbol);
                //System.out.println("*" + symbol + " -- " + as);
                //adjust the first pointer
                if(currentState == s0 && as.getState() == s0) {
                    //System.out.println("false 0 transition");
                    if(!first.next()) break;
                }
                else {
                    for(int i = 0; i < as.getAction(); ++i) {
                        first.next();
                    }
                }
                if(as.getState().getMatch() != null) {
                    AbstractSequence repl = as.getState().getMatch().getRhs();
                    if(repl instanceof Fail) {
                        System.out.println("Fail!");
                        return;
                    }
                    if(repl instanceof Succeed) {
                        System.out.println("Succeed!");
                        return;
                    }
                    if(repl instanceof Sequence) {
                        changed = true;
                        atOrPastLastChange = false; 
                        //System.out.println("==========Replacing==============" + first);
                        //System.out.println("==========Replacing==============" + second);
                        //System.out.println("in: " + l);
                        first.nonDestructiveSplice(second, (Sequence) repl);
                        if(l.isEmpty()) break DONE;
                        //System.out.println("out: " + l);
                        //System.out.println("out: " + first);
                        lastRepl = second;
                        //System.out.println("lastRepl: " + lastRepl);
                        second = first.copy();
                        //System.out.println("first: " + first);
                        //System.out.println("second: " + second);
                        currentState = s0;
                        symbol = second.get();
                        if(symbol == null) break;
                        continue;
                    }
                }
                currentState = as.getState();
                //normal transition
                if(as.getAction() == 0) {
                    if(!second.next()) break;
                    //System.out.println("*********first " + second);
                    //System.out.println("*********second " + second);
                    //System.out.println("*********lastRepl " + lastRepl);
                    if(!changed) {
                        if(second.equals(lastRepl)) {
                            atOrPastLastChange = true; 
                        }
                        if(atOrPastLastChange && currentState == s0) {
                            //System.out.println("early exit at symbol " + second);
                            break DONE;
                        }
                    }
                    symbol = second.get();
                }
                //fail transition, need to reconsider he same symbol in next state
            }
        } while(changed);
        System.out.println("substituted form = " + l.toString());
    }
    
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(needsBegin);
        sb.append("\n");
        sb.append(needsEnd);
        sb.append("\n");
        for(State state : keySet()) {
            sb.append(state);
            sb.append("\n[");
            HashMap<Symbol, ActionState> transition = get(state);
            for(Symbol symbol : transition.keySet()) {
                sb.append("  ");
                sb.append(symbol);
                sb.append(" -> ");
                sb.append(transition.get(symbol));
                sb.append("\n");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
    
    public String toDotString() {
        StringBuilder sb = new StringBuilder("digraph A");
        sb.append((long) (Math.random()* 2e61d)); 
        sb.append("{\n    rankdir=TB;\n    node [shape=circle];\n");
        // sb.append("    edge [style=\">=stealth' ,shorten >=1pt\"];\n");
        for(State state : keySet()) {
            sb.append("    ");
            sb.append(state.toFullDotString());
            sb.append("\n");
        }
        for(State state : keySet()) {
            HashMap<Symbol, ActionState> transition = get(state);
            sb.append(transitionToDotString(state, transition));
        }
        sb.append("}");
        return sb.toString();
    }
    
    public Map<Symbol, Integer> mkSymToNum() {
        HashMap<Symbol, ActionState> transition = get(s0);
        HashMap<Symbol, Integer> symToNum = new HashMap<Symbol, Integer>();
        int i = 0;
        for(Symbol key : transition.keySet()) {
            symToNum.put(key, i++); 
        }
        return symToNum;
    }
    
    public String toImplString() {
        Map<Symbol, Integer> symToNum = mkSymToNum();
        StringBuilder sb = new StringBuilder();
        sb.append(symToNum.toString());
        sb.append("\n\n");
        sb.append("static RVMPMATransitionImpl [][] pma = {");
        for(State state : keySet()) {
            sb.append("{");
            HashMap<Symbol, ActionState> transition = get(state);
            for(Symbol symbol : transition.keySet()) {
                ActionState astate = transition.get(symbol);
                State s = astate.getState();
                sb.append("new RVMPMATransitionImpl(");
                sb.append(astate.getAction());
                sb.append(", new RVMPMAStateImpl(");
                sb.append(s.getNumber());
                if(s.getMatch() != null) {
                    s.getMatch().getRhs().getImpl(sb, symToNum);
                }
                sb.append(")),\n");
            }
            sb.append("},\n\n");
        }
        sb.append("};\n");
        return sb.toString();
    }
    
    public StringBuilder transitionToDotString(State state, Map<Symbol, ActionState> transition) {
        Map<ActionState, ArrayList<Symbol>> edgeCondensingMap = new
        LinkedHashMap<ActionState, ArrayList<Symbol>>();
        
        for(Symbol symbol : transition.keySet()) {
            ActionState next = transition.get(symbol);
            ArrayList<Symbol> edges = edgeCondensingMap.get(next);
            if(edges == null) {
                edges = new ArrayList<Symbol>();
                edgeCondensingMap.put(next, edges);
            }
            edges.add(symbol);
        }
        
        //  System.out.println(edgeCondensingMap);
        
        // if(true) throw new RuntimeException();
        
        StringBuilder sb = new StringBuilder();
        
        for(ActionState next : edgeCondensingMap.keySet()) {
            sb.append("    ");
            sb.append(state.toNameDotString());
            sb.append(" -> ");
            sb.append(next.getState().toNameDotString());
            sb.append(" [label=\"");
            StringBuilder label = new StringBuilder();
            for(Symbol symbol : edgeCondensingMap.get(next)) {
                label.append(symbol.toString());
                label.append(", ");
            }
            label.setCharAt(label.length() - 1, '/');
            label.setCharAt(label.length() - 2, ' ');
            label.append(" ");
            label.append(next.getAction());
            sb.append(label);
            sb.append("\"];\n");
        }
        
        return sb;
        
        //    for(Symbol symbol : transition.keySet()) {
        //      sb.append("    ");
        //      sb.append(state.toNameDotString());
        //      sb.append(" -> ");
        //      ActionState next = transition.get(symbol);
        //      sb.append(next.getState().toNameDotString());
        //      sb.append(" [texlbl=\"$");
        //      sb.append(symbol.toDotString());
        //      sb.append(" / ");
        //      sb.append(next.getAction());
        //      sb.append("$\"];\n");
        //    } 
    }
}