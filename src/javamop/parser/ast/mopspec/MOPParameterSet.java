package javamop.parser.ast.mopspec;


import java.util.ArrayList;
import java.util.Iterator;

public class MOPParameterSet implements Iterable<MOPParameters>{
    
    public final ArrayList<MOPParameters> paramSet;
    
    public MOPParameterSet() {
        this.paramSet = new ArrayList<MOPParameters>();
    }
    
    public MOPParameterSet(ArrayList<MOPParameters> paramSet) {
        this.paramSet = new ArrayList<MOPParameters>();
        this.paramSet.addAll(paramSet);
    }
    
    /**
     * Find a parameter set with the same parameters in paramSet
     * 
     * @param set
     *            a list of parameters
     */
    public MOPParameters getParameters(MOPParameters set) {
        for (MOPParameters s : this.paramSet) {
            if (set.equals((Object)s))
                return s;
        }
        return null;
    }
    
    public void add(MOPParameters param) {
        if (getParameters(param) == null) {
            paramSet.add(param);
        }
    }
    
    public int size() {
        return this.paramSet.size();
    }
    
    public void addAll(MOPParameterSet set) {
        if (set == null || set.paramSet == null)
            return;
        for (MOPParameters param : set.paramSet) {
            this.add(param);
        }
    }
    
    public void remove(MOPParameters param){
        this.paramSet.remove(param);
    }
    
    public boolean contains(MOPParameters param){
        for(MOPParameters param2 : paramSet){
            if(param2.equals(param))
                return true;
        }
        
        return false;
    }
    
    public MOPParameters get(int i){
        return this.paramSet.get(i);
    }
    
    public void sort() {
        for (int i = 0; i < paramSet.size(); i++) {
            for (int j = i + 1; j < paramSet.size(); j++) {
                if (paramSet.get(j).contains(paramSet.get(i)) && paramSet.get(j).size() > paramSet.get(i).size()) {
                    MOPParameters temp = paramSet.get(i);
                    paramSet.set(i, paramSet.get(j));
                    paramSet.set(j, temp);
                }
            }
        }
    }
    
    public Iterator<MOPParameters> iterator(){
        return paramSet.iterator();
    }
    
    public int getIdnum(MOPParameters p){
        if(!contains(p))
            return -1;
        
        return this.paramSet.indexOf(p);
    }
    
    public String toString(){
        return paramSet.toString();
    }
    
}
