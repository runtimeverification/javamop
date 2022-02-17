/// TODO : consider symbols that don't appear in the SRS?

package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//This uses a slightly modified Aho-Corasick automaton
public class ShellPatternMatchAutomaton extends
        LinkedHashMap<State, HashMap<Symbol, ActionState>> {
    private State s0;
    private boolean hasBegin = false;
    private boolean hasEnd = false;
    private Map<Symbol, Integer> symToNum = null;

    public ShellPatternMatchAutomaton(State s0) {
        this.s0 = s0;
    }

    public void setBegin(boolean b) {
        hasBegin = b;
    }

    public void setEnd(boolean b) {
        hasEnd = b;
    }

    public boolean hasBegin() {
        return hasBegin;
    }

    public boolean hasEnd() {
        return hasEnd;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(hasBegin);
        sb.append("\n");
        sb.append(hasEnd);
        sb.append("\n");
        for (State state : keySet()) {
            sb.append(state);
            sb.append("\n[");
            HashMap<Symbol, ActionState> transition = get(state);
            for (Symbol symbol : transition.keySet()) {
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

    private Map<Symbol, Integer> mkSymToNum() {
        HashMap<Symbol, ActionState> transition = get(s0);
        HashMap<Symbol, Integer> ret = new HashMap<Symbol, Integer>();
        int i = 0;
        for (Symbol key : transition.keySet()) {
            ret.put(key, i++);
        }
        return ret;
    }

    public Map<Symbol, Integer> getSymToNum() {
        if (symToNum == null) {
            symToNum = mkSymToNum();
        }
        return symToNum;
    }

    public String toImplString() {
        Map<Symbol, Integer> symToNum = mkSymToNum();
        StringBuilder sb = new StringBuilder();
        // sb.append(symToNum.toString());
        sb.append("\n\n");
        sb.append("static RVMPMATransitionImpl [][] $pma$ = {\n");
        for (State state : keySet()) {
            sb.append("{\n");
            HashMap<Symbol, ActionState> transition = get(state);
            for (Symbol symbol : transition.keySet()) {
                ActionState astate = transition.get(symbol);
                State s = astate.getState();
                sb.append("new RVMPMATransitionImpl(");
                sb.append(astate.getAction());
                sb.append(", new RVMPMAStateImpl(");
                sb.append(s.getNumber());
                if (s.getMatch() != null) {
                    s.getMatch().getRhs().getImpl(sb, symToNum);
                }
                sb.append(")),\n");
            }
            sb.append("},\n\n");
        }
        sb.append("};\n");
        return sb.toString();
    }
}
