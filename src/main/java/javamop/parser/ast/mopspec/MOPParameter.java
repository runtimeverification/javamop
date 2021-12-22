// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import com.github.javaparser.TokenRange;
import javamop.parser.ast.aspectj.TypePattern;
import javamop.parser.astex.ExtNode;

public class MOPParameter extends ExtNode {
    private final TypePattern type;
    private final String name;
    
    public MOPParameter (TokenRange tokenRange, TypePattern type, String name){
        super(tokenRange);
        this.type = type;
        this.name = name;
    }
    
    public TypePattern getType() {return type;}
    public String getName() {return name;}
    
    public boolean equals(MOPParameter param){
        return type.equals(param.getType()) && name.equals(param.getName());
    }
    
}
