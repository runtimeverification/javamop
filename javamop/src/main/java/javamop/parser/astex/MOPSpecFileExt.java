// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.visitor.DumpVisitor;

public class MOPSpecFileExt extends ExtNode {
    private final PackageDeclaration pakage;
    private final List<ImportDeclaration> imports;
    private final List<JavaMOPSpecExt> specList;
    
    public MOPSpecFileExt(TokenRange tokenRange, final PackageDeclaration pakage,
                          final List<ImportDeclaration> imports, final List<JavaMOPSpecExt> specList) {
        super(tokenRange);
        this.pakage = pakage;
        this.imports = Collections.unmodifiableList(new ArrayList<>(imports));
        this.specList = Collections.unmodifiableList(new ArrayList<>(specList));
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

    public <A> void accept(DumpVisitor v, A arg) {
            v.visit(this, (Void) arg);
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
}
