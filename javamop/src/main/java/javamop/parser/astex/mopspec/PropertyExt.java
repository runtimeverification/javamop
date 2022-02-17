// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import java.util.Objects;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;

public abstract class PropertyExt extends ExtNode {
    
    private final String type;
    private final String propertyName; //soha
    
    public PropertyExt (TokenRange tokenRange, String type, String propertyName){
        super(tokenRange);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PropertyExt that = (PropertyExt) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(propertyName, that.propertyName);
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof javamop.parser.ast.visitor.MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }

    //TODO: start
    @Override
    public int hashCode() {
        return Objects.hash(type, propertyName);
    }
}
