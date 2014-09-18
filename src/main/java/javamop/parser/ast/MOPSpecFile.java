// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class MOPSpecFile extends Node {
    private final PackageDeclaration pakage;
    private final List<ImportDeclaration> imports;
    private final List<JavaMOPSpec> specList;
    
    public MOPSpecFile(final int line, final int column, final PackageDeclaration pakage, 
            final List<ImportDeclaration> imports, final List<JavaMOPSpec> specList) {
        super(line, column);
        this.pakage = pakage;
        this.imports = Collections.unmodifiableList(new ArrayList<ImportDeclaration>(imports));
        this.specList = Collections.unmodifiableList(new ArrayList<JavaMOPSpec>(specList));
    }
    public PackageDeclaration getPakage() {
        return pakage;
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }
	
    public List<JavaMOPSpec> getSpecs() {
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
}
