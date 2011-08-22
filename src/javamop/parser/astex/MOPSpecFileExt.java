package javamop.parser.astex;

import java.util.List;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

public class MOPSpecFileExt extends ExtNode {
	PackageDeclaration pakage = null;
	List<ImportDeclaration> imports = null;
	List<JavaMOPSpecExt> specList = null;
	
    public MOPSpecFileExt(int line, int column, PackageDeclaration pakage, List<ImportDeclaration> imports, List<JavaMOPSpecExt> specList) {
        super(line, column);
        this.pakage = pakage;
        this.imports = imports;
        this.specList = specList;
    }
    public MOPSpecFileExt() {
		super(0,0);
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
     * returns the JavaMOPSpecExt object for a specification with a specified name
     *
     */
  
	public JavaMOPSpecExt getSpec(String name) {
		for(JavaMOPSpecExt spec:this.getSpecs()){
			if(spec.getName().compareTo(name)==0)
				return spec;
		}
		return null;
	}

}
