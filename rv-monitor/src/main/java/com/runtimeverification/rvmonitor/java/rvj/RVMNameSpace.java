package com.runtimeverification.rvmonitor.java.rvj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvmonitor.util.RVMException;

public class RVMNameSpace {

    static private boolean used = false;
    static private List<String> userVariables = new ArrayList<>();
    static private List<String> mopVariables = new ArrayList<>();
    static private Map<String, String> mapVars = new HashMap<>();

    static private final HashSet<String> keywords = new HashSet<String>();

    static {
        keywords.add("this");
        keywords.add("null");
        keywords.add("RVM_lastevent");
        keywords.add("RVM_terminated");
    }

    static public void init() {
        used = false;
        userVariables = new ArrayList<>();
        mopVariables = new ArrayList<>();
        mapVars = new HashMap<>();
    }

    static public void addUserVariable(String varName) throws RVMException {
        if (used)
            throw new RVMException("Cannot update RVMNameSpace after once used");

        if (keywords.contains(varName))
            throw new RVMException(varName + " is reserved in "
                    + "RV Monitor. Please rename it.");

        if (!userVariables.contains(varName))
            userVariables.add(varName);
    }

    static public boolean checkUserVariable(String varName) {
        return userVariables.contains(varName);
    }

    static public String getRVMVar(String varName) {
        used = true;

        String cachedVar = mapVars.get(varName);
        if (cachedVar != null)
            return cachedVar;

        if (keywords.contains(varName))
            return varName;

        int extNum = 1;
        if (userVariables.contains(varName) || mopVariables.contains(varName)) {
            while (userVariables.contains(varName + "_" + extNum)
                    || mopVariables.contains(varName + "_" + extNum))
                extNum++;
            mapVars.put(varName, varName + "_" + extNum);
            mopVariables.add(varName + "_" + extNum);
            return varName + "_" + extNum;
        } else {
            mapVars.put(varName, varName);
            return varName;
        }
    }

}
