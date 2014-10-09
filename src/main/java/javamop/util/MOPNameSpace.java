// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A namespace class to keep track of which variable names are being used to avoid duplicate
 * variables with the same name.
 */
public final class MOPNameSpace {
    
    /**
     * Private to prevent instantiation.
     */
    private MOPNameSpace() {
        
    }
    
    static private boolean used = false;
    static private final List<String> userVariables = new ArrayList<String>();
    static private final List<String> mopVariables = new ArrayList<String>();
    static private final Map<String, String> mapVars = new HashMap<String, String>();
    
    static private final HashSet<String> keywords = new HashSet<String>();
    
    static {
        keywords.add("this");
        keywords.add("null");
        keywords.add("MOP_lastevent");
        keywords.add("MOP_terminated");
        keywords.add("thisJoinPoint");
        
    }
    
    /**
     * Reinitialize the static data.
     */
    static public void init(){
        used = false;
    }
    
    /**
     * Register a new user variable.
     * @param varName The name of the user variable.
     * @throws javamop.util.MOPException If the name is reserved in JavaMOP.
     */
    static public void addUserVariable(final String varName) throws MOPException {
        if (used)
            throw new MOPException("Cannot update MOPNameSpace after once used");
        
        if(keywords.contains(varName))
            throw new MOPException(varName + " is reserved in JavaMOP/AspectJ. Please rename it.");
        
        if(!userVariables.contains(varName))
            userVariables.add(varName);
    }
    
    /**
     * Add a collection of user variables.
     * @param varNames A collection of user variable names.
     * @throws MOPException If any of the names is reserved in JavaMOP.
     */
    static public void addUserVariables(final Collection<String> varNames) throws MOPException {
        for (String varName : varNames)
            addUserVariable(varName);
    }
    
    /**
     * Check the existence of a named user variable.
     * @param varName The name of the variable to look for.
     * @return Whether there exists a user variable by that name or not.
     */
    static public boolean checkUserVariable(final String varName) {
        return userVariables.contains(varName);
    }
    
    /**
     * Retrieve a unique variable name with a given base for use in JavaMOP code.
     * @param varName The base of the variable name.
     * @return A unique name to use at a particular instnace.
     */
    static public String getMOPVar(final String varName) {
        used = true;
        
        String cachedVar = mapVars.get(varName);
        if (cachedVar != null)
            return cachedVar;
        
        if(keywords.contains(varName))
            return varName;
        
        int extNum = 1;
        if (userVariables.contains(varName) || mopVariables.contains(varName)) {
            while (userVariables.contains(varName + "_" + extNum) || 
                    mopVariables.contains(varName + "_" + extNum))
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
