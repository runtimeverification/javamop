// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import java.util.Objects;

import com.github.javaparser.TokenRange;
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

    @Override
    public int hashCode() {
        return Objects.hash(type, propertyName);
    }
}
