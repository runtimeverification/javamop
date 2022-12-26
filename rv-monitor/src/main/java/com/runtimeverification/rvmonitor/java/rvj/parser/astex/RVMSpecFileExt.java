package com.runtimeverification.rvmonitor.java.rvj.parser.astex;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.ImportDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.PackageDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.RVMonitorSpecExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;

public class RVMSpecFileExt extends ExtNode {
    private final PackageDeclaration pakage;
    private final List<ImportDeclaration> imports;
    private final List<RVMonitorSpecExt> specList;

    public RVMSpecFileExt(int line, int column, PackageDeclaration pakage,
            List<ImportDeclaration> imports, List<RVMonitorSpecExt> specList) {
        super(line, column);
        this.pakage = pakage;
        this.imports = imports;
        this.specList = specList;
    }

    public PackageDeclaration getPakage() {
        return pakage;
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }

    public List<RVMonitorSpecExt> getSpecs() {
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
     * returns the RVMonitorSpecExt object for a specification with a specified
     * name
     *
     */

    public RVMonitorSpecExt getSpec(String name) {
        for (RVMonitorSpecExt spec : this.getSpecs()) {
            if (spec.getName().compareTo(name) == 0)
                return spec;
        }
        return null;
    }

}
