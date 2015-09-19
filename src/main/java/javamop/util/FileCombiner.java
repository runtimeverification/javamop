// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;

import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.mopspec.JavaMOPSpec;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * Helper class to combine generated intermediate AspectJ files and Processed MOP specifications.
 * 
 * @author Qingzhou Luo
 * */
public class FileCombiner {

    /**
     * Combine multiple MOP specification files into a single one. They must all be in the same
     * package, or with some in one package and the rest with no package.
     * @param specFiles A list of specification files.
     * @return A single aggregate MOPSpecFile.
     * @throws MOPException When the specification files are in conflicting packages.
     */
    public static MOPSpecFile combineSpecFiles(ArrayList<MOPSpecFile> specFiles) throws MOPException {
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
    
    private static List<String> combineList(List<String> one, List<String> two) {
        List<String> result = new ArrayList<String>(one);
        for (String s : two) {
            if (!result.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }
}
