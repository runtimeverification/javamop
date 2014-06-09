package javamop.output.combinedaspect.event;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.event.advice.AdviceBody;
import javamop.parser.ast.aspectj.TypePattern;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class EndObject {
    JavaMOPSpec mopSpec;
    EventDefinition event;
    
    String endObjectVar;
    TypePattern endObjectType;
    
    boolean isStart;
    AdviceBody eventBody = null;
    
    MOPVariable endObjectSupportType;
    
    public EndObject(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
        if (!event.isEndObject())
            throw new MOPException("EndObject should be defined only for endObject pointcut.");
        
        this.mopSpec = mopSpec;
        this.event = event;
        
        this.endObjectType = event.getEndObjectType();
        this.endObjectVar = event.getEndObjectVar();
        if (this.endObjectVar == null || this.endObjectVar.length() == 0)
            throw new MOPException("The variable for an endObject pointcut is not defined.");
        this.endObjectSupportType = new MOPVariable(endObjectType.toString() + "MOPFinalized"); 
        
        this.isStart = event.isStartEvent();
        
        MOPParameter endParam = event.getMOPParametersOnSpec().getParam(event.getEndObjectVar());
        MOPParameters endParams = new MOPParameters();
        if (endParam != null)
            endParams.add(endParam);
        
        this.eventBody = new AdviceBody(mopSpec, event, combinedAspect);
    }
    
    public String printDecl() {
        String ret = "";
        
        ret += "public static abstract class " + endObjectSupportType + "{\n";
        ret += "protected void finalize() throws Throwable{\n";
        ret += "try {\n";
        ret += endObjectType + " " + endObjectVar + " = (" + endObjectType + ")this;\n";
        // CHECK WITH ENDOBJECT
        ret += eventBody;
        ret += "} finally {\n";
        ret += "super.finalize();\n";
        ret += "}\n";
        ret += "}\n"; //method
        ret += "}\n"; //abstract class
        ret += "\n";
        
        ret += "declare parents : " + endObjectType + " extends " + endObjectSupportType + ";\n";
        ret += "\n";
        
        ret += "after(" + endObjectType + " " + endObjectVar + ") : execution(void " + endObjectType + ".finalize()) && this(" + endObjectVar + "){\n";
        ret += eventBody;
        ret += "}\n";
        
        return ret;
    }
    
    
}
