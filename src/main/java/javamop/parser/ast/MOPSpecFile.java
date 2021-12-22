// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.astex.ExtNode;

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

}
