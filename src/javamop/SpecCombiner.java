package javamop;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class SpecCombiner {
    
    static public MOPSpecFile process(ArrayList<MOPSpecFile> specFiles) throws MOPException{
        PackageDeclaration pakage = null;
        List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
        List<JavaMOPSpec> specList = new ArrayList<JavaMOPSpec>();
        
        for(MOPSpecFile specFile : specFiles){
            //package decl
            PackageDeclaration pakage2 = specFile.getPakage();
            if(pakage == null)
                pakage = pakage2;
            else {
                if(!pakage2.getName().getName().equals(pakage.getName().getName()))
                    throw new MOPException("Specifications need to be in the same package to " +
                        "be combined.");
            }
            
            //imports
            List<ImportDeclaration> imports2 = specFile.getImports();
            
            for(ImportDeclaration imp2 : imports2){
                boolean included = false;
                for(ImportDeclaration imp : imports){
                    if(imp2.getName().getName().equals(imp.getName().getName())){
                        included = true;
                        break;
                    }
                }
                
                if(!included)
                    imports.add(imp2);
            }
            
            //specs
            List<JavaMOPSpec> specList2 = specFile.getSpecs();
            
            for(JavaMOPSpec spec2 : specList2){
                boolean included = false;
                for(JavaMOPSpec spec : specList){
                    if(spec2.getName().equals(spec.getName())){
                        included = true;
                        break;
                    }
                }
                
                if(!included)
                    specList.add(spec2);
            }
        }
        
        return new MOPSpecFile(0, 0, pakage, imports, specList);
    }
}
