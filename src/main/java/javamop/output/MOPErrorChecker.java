// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output;

import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.util.MOPException;

final class MOPErrorChecker {
    
    /**
     * Private to prevent instantiation.
     */
    private MOPErrorChecker() {
        
    }
    
    /**
     * Verify that certain properties about the specification are true.
     * @param mopSpec The specification to verify the properties of.
     * @throws javamop.util.MOPException If some properties are not met.
     */
    public static void verify(final JavaMOPSpec mopSpec) throws MOPException {
        for (EventDefinition event : mopSpec.getEvents()) {
            verifyThreadPointCut(event);
            
            //endProgram cannot have any parameter.
            verifyEndProgramParam(event);
            
            //endThread cannot have any parameter except one from thread pointcut.
            verifyEndThreadParam(event);
        }
        
        //there should be only one endProgram event
        verifyUniqueEndProgram(mopSpec);
        
        verifyGeneralParametric(mopSpec);
        
        //check if two endObject pointcuts share the same parameter, which should not happen
    }
    
    /**
     * Ensure that thread parameters are not used improperly.
     * @param event The event definition to verify.
     * @throws MOPException If the thread variable is used improperly.
     */
    public static void verifyThreadPointCut(final EventDefinition event) throws MOPException {
        final String threadVar = event.getThreadVar();
        if (threadVar == null || threadVar.length() == 0)
            return;
        
        for (MOPParameter param : event.getRetVal()) {
            if (param.getName().equals(threadVar))
                throw new MOPException("A variable from a thread pointcut cannot appear as " +
                    "the resulting variable.");
        }
        
        for (MOPParameter param : event.getThrowVal()) {
            if (param.getName().equals(threadVar))
                throw new MOPException("A variable from a thread pointcut cannot appear as " +
                    "the throwing variable.");
        }
    }
    
    /**
     * Verify there is only one endProgram event.
     * @param mopSpec The specification to verify.
     * @throws MOPException if there is more than one endProgram event.
     */
    public static void verifyUniqueEndProgram(JavaMOPSpec mopSpec) throws MOPException {
        boolean found = false;
        
        for(EventDefinition event : mopSpec.getEvents()){
            if(event.isEndProgram()){
                if(found)
                    throw new MOPException("There can be only one endProgram event");
                else
                    found = true;
            }
        }
    }
    
    /**
     * Verify that parametric properties have at least one parameter.
     * @param mopSpec The specification to verify.
     * @throws MOPException If the specification has no parameters and is parametric.
     */
    public static void verifyGeneralParametric(JavaMOPSpec mopSpec) throws MOPException {
        if(mopSpec.isGeneral() && mopSpec.getParameters().size() == 0)
            throw new MOPException("[Internal Error] It cannot use general parameteric " +
                "algorithm when there is no parameter");
    }
    
    /**
     * Verify that an endProgram event cannot have parameters.
     * @param event The event to verify.
     * @throws MOPException If the event is an endProgram event and has parameters.
     */
    public static void verifyEndProgramParam(EventDefinition event) throws MOPException {
        if(event.isEndProgram() && event.getParameters().size() > 0)
            throw new MOPException("A endProgram pointcut cannot have any parameter.");
    }
    
    /**
     * Verify that an endThread parameter only has the thread as a parameter.
     * @param event The event to verify.
     * @throws MOPException If the endThread event doesn't have only the thread parameter.
     */
    public static void verifyEndThreadParam(EventDefinition event) throws MOPException {
        if(event.isEndThread() && event.getParametersWithoutThreadVar().size() > 0)
            throw new MOPException("A endThread pointcut cannot have any parameter except " +
                "one from thread pointcut.");
    }
    
}
