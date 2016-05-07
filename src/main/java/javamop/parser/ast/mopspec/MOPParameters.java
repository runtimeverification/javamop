// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javamop.parser.ast.aspectj.BaseTypePattern;

public class MOPParameters implements Iterable<MOPParameter>, Serializable {
    
    private final ArrayList<MOPParameter> parameters;
    
    public MOPParameters() {
        this.parameters = new ArrayList<MOPParameter>();
    }
    
    public MOPParameters(List<MOPParameter> params) {
        this.parameters = new ArrayList<MOPParameter>();
        
        if(params != null){
            for(MOPParameter param : params){
                this.add(param);
            }
        }
    }
    
    public MOPParameters(MOPParameters params) {
        this.parameters = new ArrayList<MOPParameter>();
        
        if(params != null){
            for(MOPParameter param : params){
                this.add(param);
            }
        }
    }
    
    public void add(MOPParameter p) {
        if (this.getParam(p.getName()) == null) {
            MOPParameter p2 = p;
            if (p.getType().getOp().charAt(p.getType().getOp().length() - 1) == '+') {
                BaseTypePattern t2 = new BaseTypePattern(p.getType().getBeginLine(), p.getType().getBeginColumn(), p.getType().getOp().substring(0,
                                                                                                                                                 p.getType().getOp().length() - 1));
                p2 = new MOPParameter(p.getBeginLine(), p.getBeginColumn(), t2, p.getName());
            }
            this.parameters.add(p2);
        }
    }
    
    public void addAll(MOPParameters set) {
        if (set == null)
            return;
        for (MOPParameter p : set.parameters) {
            if (this.getParam(p.getName()) == null)
                this.parameters.add(p);
        }
    }
    
    public void addAll(List<MOPParameter> set) {
        if (set == null)
            return;
        for (MOPParameter p : set) {
            if (this.getParam(p.getName()) == null) {
                MOPParameter p2 = p;
                if (p.getType().getOp().charAt(p.getType().getOp().length() - 1) == '+') {
                    BaseTypePattern t2 = new BaseTypePattern(p.getType().getBeginLine(), p.getType().getBeginColumn(), p.getType().getOp().substring(0,
                                                                                                                                                     p.getType().getOp().length() - 1));
                    p2 = new MOPParameter(p.getBeginLine(), p.getBeginColumn(), t2, p.getName());
                }
                this.parameters.add(p2);
            }
        }
    }
    
    /**
     * Find a parameter with the given name
     * 
     * @param name
     *            a parameter name
     */
    public MOPParameter getParam(String name) {
        MOPParameter ret = null;
        
        for (MOPParameter param : this.parameters) {
            if (param.getName().compareTo(name) == 0) {
                ret = param;
                break;
            }
        }
        return ret;
    }
    
    static public MOPParameters unionSet(MOPParameters set1, MOPParameters set2) {
        MOPParameters ret = new MOPParameters();
        
        if (set1 != null)
            ret.addAll(set1);
        
        if (set2 != null)
            ret.addAll(set2);
        return ret;
    }
    
    static public MOPParameters intersectionSet(MOPParameters set1, MOPParameters set2) {
        MOPParameters ret = new MOPParameters();
        
        for (MOPParameter p1 : set1) {
            for (MOPParameter p2 : set2) {
                if (p1.getName().compareTo(p2.getName()) == 0) {
                    ret.add(p1);
                    break;
                }
            }
        }
        return ret;
    }
    
    public MOPParameters sortParam(MOPParameters set) {
        MOPParameters ret = new MOPParameters();
        
        for (MOPParameter p : this.parameters) {
            if (set.contains(p))
                ret.add(p);
        }
        
        for (MOPParameter p : set.parameters) {
            if (!ret.contains(p))
                ret.add(p);
        }
        return ret;
    }
    
    public boolean contains(MOPParameter p) {
        return (this.getParam(p.getName()) != null);
    }
    
    public boolean contains(MOPParameters set) {
        for (MOPParameter p : set.parameters) {
            if (!this.contains(p))
                return false;
        }
        return true;
    }
    
    public int size() {
        return this.parameters.size();
    }
    
    /**
     * Compare a list of parameters with this one to see if they contains the
     * same parameters
     * 
     * @param set
     *            MoPParameters
     */
    public boolean equals(Object set) {
        if (!(set instanceof MOPParameters))
            return false;
        
        return this.equals((MOPParameters) set);
    }
    
    public boolean equals(MOPParameters set) {
        if(set == null)
            return false;
        if (this.size() != set.size())
            return false;
        return this.contains(set) && set.contains(this);
    }
    
    public boolean matchTypes(MOPParameters set){
        if(this.size() != set.size())
            return false;
        for (int i = 0; i < this.parameters.size(); i++) {
            if (this.parameters.get(i).getType().getOp().equals(set.get(i).getType().getOp())){
                return false;
            }
        }               
        
        return true;
    }
    
    
    public MOPParameter get(int i) {
        if (i < 0 || i >= this.parameters.size())
            return null;
        return this.parameters.get(i);
    }
    
    ArrayList<MOPParameter> lexico = null;
    
    public MOPParameter get_lexicographic(int i) {
        if (i < 0 || i >= this.parameters.size())
            return null;
        
        if(this.lexico == null || this.lexico.size() != this.parameters.size()){
            this.lexico = new ArrayList<MOPParameter>(this.parameters);
            Collections.sort(lexico, new Comparator<MOPParameter>() {
                public int compare(MOPParameter p1, MOPParameter p2) {
                    int ret = p1.getType().getOp().compareTo(p2.getType().getOp());
                    if(ret != 0)
                        return ret;
                    ret = p1.getName().compareTo(p2.getName());
                    return ret;
                }
            });
        }
        
        return this.lexico.get(i);
    }
    
    
    public Iterator<MOPParameter> iterator() {
        return this.parameters.iterator();
    }
    
    public String parameterString() {
        String ret = "";
        
        for (MOPParameter param : this.parameters) {
            ret += ", " + param.getName();
        }
        if (ret.length() != 0)
            ret = ret.substring(2);
        return ret;
    }
    
    public String parameterStringIn(MOPParameters all) {
        String ret = "";
        
        for (MOPParameter param : all) {
            if(this.contains(param)){
                ret += ", " + param.getName();
            } else {
                ret += ", null"; 
            }
            
        }
        
        if (ret.length() != 0)
            ret = ret.substring(2);
        
        return ret;
    }
    
    public String parameterDeclString() {
        String ret = "";
        
        for (MOPParameter param : this.parameters) {
            ret += ", " + param.getType() + " " + param.getName();
        }
        if (ret.length() != 0)
            ret = ret.substring(2);
        
        return ret;
    }
    
    public String parameterStringUnderscore() {
        String ret = "";
        
        for (MOPParameter param : this.parameters) {
            ret += "_" + param.getName();
        }
        if (ret.length() != 0)
            ret = ret.substring(1);
        
        return ret;
    }
    
    
    public int getIdnum(MOPParameter p) {
        for (int i = 0; i < this.parameters.size(); i++) {
            MOPParameter param = this.parameters.get(i);
            
            if (param.getName().compareTo(p.getName()) == 0) {
                return i;
            }
        }
        return -1;
    }
    
    public int hashCode() {
        int code = 0;
        
        for (MOPParameter param : this.parameters) {
            code ^= param.hashCode();
        }
        
        return code;
    }
    
    public String toString() {
        return parameters.toString();
    }
    
    public List<MOPParameter> toList(){
        return parameters;
    }
    
}
