package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

public class State {
    private static int counter = 0;
    private int number;
    private int depth;
    private Rule matchedRule = null;

    public State(int number, int depth, Rule matchedRule) {
        this.matchedRule = matchedRule;
        this.number = number;
        this.depth = depth;
    }

    public int getNumber() {
        return number;
    }

    public int getDepth() {
        return depth;
    }

    public Rule getMatch() {
        return matchedRule;
    }

    public void setMatch(Rule r) {
        matchedRule = r;
    }

    // matched rule must always be equal if number is equal
    // ditto with depth
    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof State))
            return false;
        State s = (State) o;
        return s.number == number;
    }

    @Override
    public String toString() {
        return "<"
                + number
                + " @ "
                + depth
                + ((matchedRule == null) ? "" : " matches "
                        + matchedRule.toString()) + ">";
    }

    static String mkSpaces(int len) {
        StringBuilder sb = new StringBuilder();
        for (; len > 0; --len) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
