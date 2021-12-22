// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class WildcardParameter extends BaseTypePattern {
    
    public WildcardParameter(TokenRange tokenRange) {
        super(tokenRange, "..");
    }
    
}
