// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/**
 * @author Feng Chen, Dongyun Jin
 * The class handling the mop specification tree
 */

package javamop.parser;

import javamop.MOPNameSpace;
import javamop.ParserService;
import javamop.output.AspectJCode;
import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.visitor.CollectUserVarVisitor;
import javamop.util.Tool;

import java.util.List;

/**
 * A class for taking in specification file objects and producing the JavaMOP additions to
 * RV-Monitor code.
 */
class MOPProcessor {
    public static boolean verbose = false;
    
    private String name;

    /**
     * Construct a MOPProcessor.
     * @param name The name of the processor.
     */
    protected MOPProcessor(String name) {
        this.name = name;
    }

    /**
     * Update the name of the MOPProcessor.
     * @param newName
     */
    protected void updateName(String newName){
        this.name=newName;
    }

    /**
     * Register user variables into the MOPNameSpace so that generated code does not clash with
     * them.
     * @param mopSpec The specification to extract variables from.
     * @throws javamop.parser.MOPException If something goes wrong reading or registering the variables.
     */
    private void registerUserVar(JavaMOPSpec mopSpec) throws ParserService.MOPExceptionImpl {
        for (EventDefinition event : mopSpec.getEvents()) {
            MOPNameSpace.addUserVariable(event.getId());
            for(MOPParameter param : event.getMOPParameters()){
                MOPNameSpace.addUserVariable(param.getName());
            }
        }
        for (MOPParameter param : mopSpec.getParameters()) {
            MOPNameSpace.addUserVariable(param.getName());
        }
        MOPNameSpace.addUserVariable(mopSpec.getName());
        for (BodyDeclaration bd : mopSpec.getDeclarations()) {
            List<String> vars = bd.accept(new CollectUserVarVisitor(), null);
            
            if (vars != null)
                MOPNameSpace.addUserVariables(vars);
        }
    }
    
    /**
     * Convert a MOPSpecFile into .rvm file and remove all the aspectJ related parts
     * @param mopSpecFile The parameter to convert.
     * @return The generated .rvm file string
     * @throws MOPException If there is a logic error in conversion.
     */
    protected String generateRVFile(MOPSpecFile mopSpecFile) throws MOPException {
        String rvresult = "";
        if (mopSpecFile.getPakage() != null) {
            rvresult += mopSpecFile.getPakage().toString();
        }
        if (mopSpecFile.getImports() != null) {
            for (ImportDeclaration id : mopSpecFile.getImports()) {
                rvresult += id.toString();
            }
        }
        for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs()){
            rvresult += mopSpec.toString();
        }
        rvresult = Tool.changeIndentation(rvresult, "", "\t");
        return rvresult;
    }
    
    /**
     * Convert a MOPSpecFile into aspectJ file.
     * @param mopSpecFile The parameter to convert.
     * @return The generated aspectJ file.
     * @throws MOPException If there is a logic error in conversion.
     */
    protected String generateAJFile(MOPSpecFile mopSpecFile) throws ParserService.MOPExceptionImpl {
        String result = "";
        
        // register all user variables to MOPNameSpace to avoid conflicts
        for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs())
            registerUserVar(mopSpec);
        
        // Error Checker
        for(JavaMOPSpec mopSpec : mopSpecFile.getSpecs()){
            MOPErrorChecker.verify(mopSpec);
        }
        
        // Generate output code

        result = (new AspectJCode(name, mopSpecFile)).toString();

        // Do indentation
        result = Tool.changeIndentation(result, "", "\t");
        
        return result;
    }
}
