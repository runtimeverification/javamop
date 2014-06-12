package javamop.output.combinedaspect.event.advice;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class AdviceBody {
    final JavaMOPSpec mopSpec;
    public final EventDefinition event;
    public final String specName;
    public final String fileName;
    
    public final MOPStatistics stat;
    
    public final boolean isGeneral;
    
    public final boolean isFullParam;
    private final CombinedAspect aspect;
    
    public AdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) {
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
    
    public JavaMOPSpec getMOPSpec() {
        return mopSpec;
    }
    
    public EventDefinition getEvent() {
        return event;
    }
    
    public String getSpecName() {
        return specName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public MOPStatistics getStat() {
        return stat;
    }
    
    public boolean getGeneral() {
        return isGeneral;
    }
    
    public boolean getFullParam() {
        return isFullParam;
    }
}
