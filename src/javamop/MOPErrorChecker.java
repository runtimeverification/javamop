package javamop;

import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class MOPErrorChecker {
    
    public static void verify(JavaMOPSpec mopSpec) throws MOPException {
        for (EventDefinition event : mopSpec.getEvents()) {
            verifyThreadPointCut(event);
            
            //endProgram cannot have any parameter.
            verifyEndProgramParam(event);
            
            //endThread cannot have any parameter except one from thread pointcut.
            verifyEndThreadParam(event);
        }
        
        //there should be only one endProgram event
        verifyUniqueEndProgram(mopSpec);
        
        verifySameEventName(mopSpec);
        verifyGeneralParametric(mopSpec);
        
        //check if two endObject pointcuts share the same parameter, which should not happen
        
        
    }
    
    public static void verifyThreadPointCut(EventDefinition event) throws MOPException {
        String threadVar = event.getThreadVar();
        if (threadVar == null || threadVar.length() == 0)
            return;
        
        for (MOPParameter param : event.getRetVal()) {
            if (param.getName().equals(threadVar))
                throw new MOPException("A variable from a thread pointcut cannot appear as the resulting variable.");
        }
        
        for (MOPParameter param : event.getThrowVal()) {
            if (param.getName().equals(threadVar))
                throw new MOPException("A variable from a thread pointcut cannot appear as the throwing variable.");
        }
    }
    
    public static void verifySameEventName(JavaMOPSpec mopSpec) throws MOPException {
        // This check was necessary becuase previously multiple events with the same name
        // but different parameters were banned. Now that such events are allowed, this
        // code is not used.
        /*
         *        HashMap<String, MOPParameters> nameToParam = new HashMap<String, MOPParameters>();
         *        
         *        for(EventDefinition event : mopSpec.getEvents()){
         *            if(nameToParam.get(event.getId()) != null){
         *                if(!event.getMOPParametersOnSpec().equals(nameToParam.get(event.getId())))
         *                    throw new MOPException("Events with the same name should have the same parameters when projected to the specification parameters.");
    } else {
        nameToParam.put(event.getId(), event.getMOPParametersOnSpec());
    }
    }
    */
    }
    
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
    
    public static void verifyGeneralParametric(JavaMOPSpec mopSpec) throws MOPException {
        if(mopSpec.isGeneral() && mopSpec.getParameters().size() == 0)
            throw new MOPException("[Internal Error] It cannot use general parameteric algorithm when there is no parameter");
    }
    
    public static void verifyEndProgramParam(EventDefinition event) throws MOPException {
        if(event.isEndProgram() && event.getParameters().size() >0)
            throw new MOPException("A endProgram pointcut cannot have any parameter.");
    }
    
    public static void verifyEndThreadParam(EventDefinition event) throws MOPException {
        if(event.isEndThread())
            if(event.getParametersWithoutThreadVar().size() >0)
                throw new MOPException("A endThread pointcut cannot have any parameter except one from thread pointcut.");
    }
    
}
