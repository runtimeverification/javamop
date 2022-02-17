package com.runtimeverification.rvmonitor.java.rvj.output;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;

public class RVMJavaCode {
    private String code;
    private RVMVariable monitorName = null;
    private PropertyAndHandlers prop = null;
    private Set<String> localVars;

    public RVMJavaCode(String code) {
        this.code = code;
        if (this.code != null)
            this.code = this.code.trim();
    }

    public RVMJavaCode(String code, RVMVariable monitorName) {
        this.code = code;
        if (this.code != null)
            this.code = this.code.trim();
        this.monitorName = monitorName;
    }

    public RVMJavaCode(PropertyAndHandlers prop, String code,
            RVMVariable monitorName) {
        this.prop = prop;
        this.code = code;
        if (this.code != null)
            this.code = this.code.trim();
        this.monitorName = monitorName;
    }

    public RVMJavaCode(PropertyAndHandlers prop, String code,
            RVMVariable monitorName, Set<String> localVars) {
        this(prop, code, monitorName);
        this.localVars = localVars;
    }

    public String rewriteVariables(String input) {
        String ret = input;
        String tagPattern = "\\$(\\w+)\\$";
        Pattern pattern = Pattern.compile(tagPattern);
        Matcher matcher = pattern.matcher(ret);

        while (matcher.find()) {
            String tagStr = matcher.group();
            String varName = tagStr.replaceAll(tagPattern, "$1");
            RVMVariable var;

            if (prop == null)
                var = new RVMVariable(varName);
            else {
                if (localVars != null && localVars.contains(varName))
                    var = new RVMVariable(varName);
                else
                    var = new RVMVariable("Prop_" + prop.getPropertyId() + "_"
                            + varName);
            }

            ret = ret.replaceAll(tagStr.replaceAll("\\$", "\\\\\\$"),
                    var.toString());
        }
        return ret;
    }

    /**
     * Returns the name of the variable for holding the state. This variable is
     * used to determine the slot in a set of monitors. If multiple variables
     * are needed to store states, this method returns null, meaning that the
     * partitioned-set optimization cannot be used. Since it seems JavaMOP does
     * not parse the given string, I do a similar unreliable and dirty string
     * manipulation here.
     *
     * @return the name of the variable for holding the state
     */
    public String extractStateVariable() {
        String ret = this.code;
        String tagPattern = "\\$(\\w+)\\$";
        Pattern pattern = Pattern.compile(tagPattern);
        Matcher matcher = pattern.matcher(ret);

        String varname = null;

        while (matcher.find()) {
            String tagStr = matcher.group();
            String varName = tagStr.replaceAll(tagPattern, "$1");
            RVMVariable var;

            if (!varName.startsWith("state"))
                continue;

            if (prop == null)
                var = new RVMVariable(varName);
            else {
                if (localVars != null && localVars.contains(varName))
                    var = new RVMVariable(varName);
                else
                    var = new RVMVariable("Prop_" + prop.getPropertyId() + "_"
                            + varName);
            }

            // This method works only if there is a single variable.
            if (varname != null)
                return null;
            varname = var.toString();
        }
        return varname;
    }

    public String extractTableVariable() {
        String pattern = "(\\$transition_\\w+\\$)\\[";
        Matcher matcher = Pattern.compile(pattern).matcher(this.code);

        String tablename = null;

        while (matcher.find()) {
            // This method assumes that there is only one table.
            if (tablename != null)
                return null;

            tablename = matcher.group(1);
        }

        String unescaped = this.getProperJavaCode(tablename);
        return unescaped.trim();
    }

    public int getNumberOfStates() {
        // String pattern = "\\$transition_\\w+\\$\\[\\]";
        // String pattern = "\\$transition_\\w+\\$\\[\\] = ";
        String pattern = "\\$transition_\\w+\\$\\[\\] = \\{([ ,\\d]+)\\}";
        Matcher matcher = Pattern.compile(pattern).matcher(this.code);

        int maxstate = -1;

        while (matcher.find()) {
            String tbl = matcher.group(1);
            for (String tostr : tbl.split(",")) {
                int to = Integer.parseInt(tostr.trim());
                maxstate = Math.max(to, maxstate);
            }
        }

        // For the sake of the initial state.
        ++maxstate;

        return maxstate;
    }

    public int getStateRHS() {
        String pattern = "\\$state\\$ = ([\\d]+);";
        String code = this.code.trim();
        Matcher matcher = Pattern.compile(pattern).matcher(code);

        if (!matcher.find())
            throw new IllegalArgumentException();

        // $state$ = 1; should be the only thing in the code.
        {
            int start = matcher.regionStart();
            int end = matcher.regionEnd();
            if (start != 0 || end != code.length())
                throw new IllegalArgumentException();
        }

        int rhs;
        try {
            String rhsstr = matcher.group(1);
            rhs = Integer.parseInt(rhsstr);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        return rhs;
    }

    public String getWithoutState() {
        // "$state$ = 0;" -> ""
        String pattern = "\\$state\\$ = ([\\d]+);";
        Matcher matcher = Pattern.compile(pattern).matcher(this.code);
        String eliminated = matcher.replaceAll("");

        return this.getProperJavaCode(eliminated);
    }

    public String getWithoutStateDeclaration() {
        // "int $state$;" -> ""
        String pattern = "int \\$state\\$;";
        Matcher matcher = Pattern.compile(pattern).matcher(this.code);
        String eliminated = matcher.replaceAll("");

        return this.getProperJavaCode(eliminated);
    }

    public String replaceStateVariable(String stateVarName) {
        // ... $state$ ... -> ... stateVarName ...
        String pattern = "\\$state\\$";
        Matcher matcher = Pattern.compile(pattern).matcher(this.code);
        return matcher.replaceAll(stateVarName);
    }

    public boolean isEmpty() {
        if (code == null || code.length() == 0)
            return true;
        else
            return false;
    }

    private String getProperJavaCode(String rawcode) {
        String ret = rawcode;

        if (this.monitorName != null)
            ret = ret.replaceAll("\\@MONITORCLASS", monitorName.toString());

        ret = rewriteVariables(ret);

        if (ret.length() != 0 && !ret.endsWith("\n"))
            ret += "\n";

        return ret;

    }

    @Override
    public String toString() {
        String ret = "";

        if (code != null)
            ret += code;

        return this.getProperJavaCode(ret);
    }
}
