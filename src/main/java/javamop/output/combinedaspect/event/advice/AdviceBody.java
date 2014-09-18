// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event.advice;

import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

/**
 * AspectJ advice for a particular event.
 */
public class AdviceBody {
    final JavaMOPSpec mopSpec;
    public final EventDefinition event;
    public final String specName;
    public final String fileName;
    
    public final MOPStatistics stat;
    
    public final boolean isGeneral;
    
    public final boolean isFullParam;
    private final CombinedAspect aspect;
    
    /**
     * Construct an advice body for an event.
     * @param mopSpec The specification the event is a part of.
     * @param event The event the advice is for.
     * @param combinedAspect The aspect being generated.
     */
    public AdviceBody(final JavaMOPSpec mopSpec, final EventDefinition event, 
            final CombinedAspect combinedAspect) {
        this.mopSpec = mopSpec;
        this.specName = mopSpec.getName();
        this.aspect = combinedAspect;
        this.event = event;
        MOPParameters eventParams = event.getMOPParametersOnSpec();
        this.stat = combinedAspect.statManager.getStat(mopSpec);
        this.isGeneral = mopSpec.isGeneral();
        this.isFullParam = eventParams.equals(mopSpec.getParameters());
        this.fileName = combinedAspect.getFileName();
    }
    
    /**
     * The specification the advice is a part of.
     * @return The event's specification.
     */
    public JavaMOPSpec getMOPSpec() {
        return mopSpec;
    }
    
    /**
     * The event this advice is for.
     * @return The owning event.
     */
    public EventDefinition getEvent() {
        return event;
    }
    
    /**
     * The name of the specification this advice is for.
     * @return The specification name.
     */
    public String getSpecName() {
        return specName;
    }
    
    /**
     * The name of the file being generated.
     * @return The generated filename.
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * The statistics object for the event.
     * @return The statistics code generator for this event.
     */
    public MOPStatistics getStat() {
        return stat;
    }
    
    /**
     * If the specification is general or not.
     * @return The generality of the specification.
     */
    public boolean getGeneral() {
        return isGeneral;
    }
    
    /**
     * If the event has the same parameters as the specification.
     * @return If the event parameters match the specification parameters.
     */
    public boolean getFullParam() {
        return isFullParam;
    }
}
