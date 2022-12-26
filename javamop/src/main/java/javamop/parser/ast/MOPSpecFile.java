// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;
import javamop.parser.astex.visitor.RVDumpVisitor;

public class MOPSpecFile extends ExtNode {
    private final PackageDeclaration pakage;
    private final List<ImportDeclaration> imports;
    private final List<JavaMOPSpec> specList;
    
    public MOPSpecFile(TokenRange tokenRange, final PackageDeclaration pakage,
            final List<ImportDeclaration> imports, final List<JavaMOPSpec> specList) {
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
	
    public List<JavaMOPSpec> getSpecs() {
        return specList;
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

    public String toRVString() {
        RVDumpVisitor visitor = new RVDumpVisitor(new DefaultPrinterConfiguration());
        accept(visitor, null);
        return visitor.getSource();
    }

}
