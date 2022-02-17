package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

public class State {
    private static int counter = 0;
    private int number;
    private int depth;
    private Rule matchedRule = null;
    
    public State(int depth) {
        number = counter++;
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
    
    //matched rule must always be equal if number is equal
    //ditto with depth
    @Override
    public int hashCode() {
        return number;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof State)) return false;
        State s = (State) o;
        return s.number == number;
    }
    
    @Override
    public String toString() {
        return "<" + number 
        + " @ " 
        + depth 
        + ((matchedRule == null)?
        ""
        : " matches " + matchedRule.toString()
        ) 
        + ">";
    }
    
    public String toFullDotString() {
        String name = toNameDotString();
        String ruleStr;
        int ruleLen; 
        if(matchedRule == null) {
            ruleStr = "";
            ruleLen = 0;
        }
        else {
            ruleStr = "\\\\ (" + matchedRule.toDotString() + ")";
            ruleLen = matchedRule.dotLength() + 4; //add a bit extra padding
        }
        String texlbl= number + " : " + depth 
        + ruleStr;
        return name + " [texlbl=\"$\\begin{array}{c}" + texlbl 
        + "\\end{array}$\" label=\"" 
        + mkSpaces(Math.max(new Integer(number).toString().length()
        + new Integer(depth).toString().length() + 5, 
                            ruleLen)) + "\"];";
    }
    
    static String mkSpaces(int len) {
        StringBuilder sb = new StringBuilder();
        for(; len > 0; --len) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public String toNameDotString() {
        return "s_" + number;
    }
}