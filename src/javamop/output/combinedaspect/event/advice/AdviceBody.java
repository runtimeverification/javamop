package javamop.output.combinedaspect.event.advice;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class AdviceBody {
    JavaMOPSpec mopSpec;
    public EventDefinition event;
    public MOPVariable monitorName;
    public String specName;
    public String fileName;
    
    public MOPStatistics stat;
    
    public boolean isGeneral;
    MOPParameters eventParams;
    
    public boolean isFullParam;
    CombinedAspect aspect;
    
    public AdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) {
        this.mopSpec = mopSpec;
        this.specName = mopSpec.getName();
        this.aspect = combinedAspect;
        this.event = event;
        this.eventParams = event.getMOPParametersOnSpec();
        this.stat = combinedAspect.statManager.getStat(mopSpec);
        this.isGeneral = mopSpec.isGeneral();
        this.isFullParam = eventParams.equals(mopSpec.getParameters());
        this.fileName = combinedAspect.getFileName();
    }
}
