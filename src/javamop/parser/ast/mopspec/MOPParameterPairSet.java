package javamop.parser.ast.mopspec;

import java.util.*;

import javamop.parser.ast.mopspec.MOPParameters;

public class MOPParameterPairSet implements Iterable<MOPParameterPair>{
    
    public final ArrayList<MOPParameterPair> paramPairSet;
    
    public MOPParameterPairSet() {
        this.paramPairSet = new ArrayList<MOPParameterPair>();
    }
    
    public MOPParameterPair getParameterPair(MOPParameters param1, MOPParameters param2) {
        for (MOPParameterPair paramPair : paramPairSet) {
            if(paramPair.getParam1().equals((Object)param1) && paramPair.getParam2().equals((Object)param2)){
                return paramPair;
            }
        }
        return null;
    }
    
    public void add(MOPParameters param1, MOPParameters param2) {
        if (getParameterPair(param1, param2) == null) {
            paramPairSet.add(new MOPParameterPair(param1, param2));
        }
    }
    
    public void add(MOPParameterPair paramPair) {
        if (getParameterPair(paramPair.getParam1(), paramPair.getParam2()) == null) {
            paramPairSet.add(paramPair);
        }
    }
    
    public void addAll(MOPParameterPairSet paramPairSet){
        for(MOPParameterPair paramPair : paramPairSet){
            this.add(paramPair);
        }
    }
    
    public Iterator<MOPParameterPair> iterator(){
        return paramPairSet.iterator();
    }
    
    public String toString(){
        return paramPairSet.toString();
    }
}
