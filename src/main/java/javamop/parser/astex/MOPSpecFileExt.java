// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

public class MOPSpecFileExt extends ExtNode {
    private final PackageDeclaration pakage;
    private final List<ImportDeclaration> imports;
    private final List<JavaMOPSpecExt> specList;
    
    public MOPSpecFileExt(final int line, final int column, final PackageDeclaration pakage, 
            final List<ImportDeclaration> imports, final List<JavaMOPSpecExt> specList) {
        super(line, column);
        this.pakage = pakage;
        this.imports = Collections.unmodifiableList(new ArrayList<ImportDeclaration>(imports));
        this.specList = Collections.unmodifiableList(new ArrayList<JavaMOPSpecExt>(specList));
    }
    
    public PackageDeclaration getPakage() {
        return pakage;
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }
    
    public List<JavaMOPSpecExt> getSpecs() {
        return specList;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
    /**
     * Search the specifications for one with a particular name.
     * @param name The name of the desired specification
     * @return The JavaMOPSpecExt object for a specification with a specified name
     */
    public JavaMOPSpecExt getSpec(String name) {
        for(JavaMOPSpecExt spec:this.getSpecs()){
            if(spec.getName().compareTo(name)==0)
                return spec;
        }
        return null;
    }
}
