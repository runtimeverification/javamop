// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import com.github.javaparser.TokenRange;
import javamop.parser.astex.ExtNode;

public abstract class Property extends ExtNode {
    
    private final String type;
    
    public Property (TokenRange tokenRange, String type){
        super(tokenRange);
        this.type = type;
    }
    
    public String getType() { return type; }

}
