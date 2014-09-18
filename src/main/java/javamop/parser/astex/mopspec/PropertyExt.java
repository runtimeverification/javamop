// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;
import javamop.parser.astex.ExtNode;

public abstract class PropertyExt extends ExtNode {
    
    private final String type;
    private final String propertyName; //soha
    
    public PropertyExt (int line, int column, String type, String propertyName){
        super(line, column);
        this.type = type;
        this.propertyName = propertyName;
    }
    
    public String getType() {
        return type;
    }
    
    public String getName() {
        return propertyName;
    } //soha 
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
}
