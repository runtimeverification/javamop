package javamop;

import java.util.*;

public class MOPNameSpace {
    
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
    
    static public void init(){
        used = false;
    }
    
    static public void addUserVariable(String varName) throws MOPException {
        if (used)
            throw new MOPException("Cannot update MOPNameSpace after once used");
        
        if(keywords.contains(varName))
            throw new MOPException(varName + " is reserved in JavaMOP/AspectJ. Please rename it.");
        
        if(!userVariables.contains(varName))
            userVariables.add(varName);
    }
    
    static public void addUserVariables(Collection<String> varNames) throws MOPException {
        for (String varName : varNames)
            addUserVariable(varName);
    }
    
    static public boolean checkUserVariable(String varName) {
        return userVariables.contains(varName);
    }
    
    static public String getMOPVar(String varName) {
        used = true;
        
        String cachedVar = mapVars.get(varName);
        if (cachedVar != null)
            return cachedVar;
        
        if(keywords.contains(varName))
            return varName;
        
        int extNum = 1;
        if (userVariables.contains(varName) || mopVariables.contains(varName)) {
            while (userVariables.contains(varName + "_" + extNum) || mopVariables.contains(varName + "_" + extNum))
                extNum++;
            mapVars.put(varName, varName + "_" + extNum);
            mopVariables.add(varName + "_" + extNum);
            return varName + "_" + extNum;
        } else {
            mapVars.put(varName, varName);
            return varName;
        }
    }
    
    // only for debugging
    static public List<String> getUserVariables() {
        return userVariables;
    }
}
