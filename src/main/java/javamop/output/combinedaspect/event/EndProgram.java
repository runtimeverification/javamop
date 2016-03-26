// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event;

import java.util.ArrayList;

import javamop.util.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.event.advice.AdviceBody;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * An AspectJ hook in the generated code to trigger an event on the program ending.
 * Also manages hooks for threads ending.
 */
public class EndProgram {
    private final MOPVariable hookName;
    
    private final ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
    private final ArrayList<AdviceBody> eventBodies = new ArrayList<AdviceBody>();

    /**
     * Construct a named end program hook.
     * @param name The name of the hook.
     */
    public EndProgram(final String name) {
        this.hookName = new MOPVariable(name + "_DummyHookThread");
    }
    
    /**
     * Register an end program event from a specification.
     * @param mopSpec The specification with the event.
     * @param event The end program event.
     * @param combinedAspect The complete aspect that the event is part of.
     */
    public void addEndProgramEvent(final JavaMOPSpec mopSpec, final EventDefinition event, 
            final CombinedAspect combinedAspect) throws MOPException {
        if (!event.isEndProgram())
            throw new MOPException("EndProgram should be defined only for an " +
                "endProgram pointcut.");
        
        this.eventBodies.add(new AdviceBody(mopSpec, event, combinedAspect));
    }
    
    /**
     * Register end thread hooks.
     * @param endThreadEvents A list of end thread hooks.
     */
    public void registerEndThreadEvents(ArrayList<EndThread> endThreadEvents) {
        this.endThreadEvents.addAll(endThreadEvents);
    }
    
    /**
     * Java source code to add the shutdown hook.
     * @return Java source code that adds the shutdown hook to trigger events.
     */
    public String printAddStatement() {
        String ret = "";
        
        if(eventBodies.size() == 0 && endThreadEvents.size() == 0)
            return ret;
        
        ret += "Runtime.getRuntime().addShutdownHook(new " + hookName + "());\n";
        
        return ret;
    }
    
    /**
     * Java source code for the thread ending hook.
     * @return Java source code that adds the thread shutdown hook for all threads.
     */
    public String printHookThread() {
        String ret = "";
        
        if(eventBodies.size() == 0 && endThreadEvents.size() == 0)
            return ret;
        
        ret += "class " + hookName + " extends Thread {\n";
        ret += "public void run(){\n";
        
        if (endThreadEvents != null && endThreadEvents.size() > 0) {
            for (EndThread endThread : endThreadEvents) {
                ret += endThread.printAdviceBodyAtEndProgram();
            }
        }
        
        for (AdviceBody eventBody : eventBodies) {
            if (eventBodies.size() > 1) {
                ret += "{\n";
            }
            
            ret += EventManager.EventMethodHelper.methodName(eventBody.getMOPSpec(), eventBody.event,
                eventBody.fileName);
            ret += "();\n";
            
            if (eventBodies.size() > 1) {
                ret += "}\n";
            }
        }
        
        ret += "}\n";
        ret += "}\n";
        
        return ret;
    }
}
