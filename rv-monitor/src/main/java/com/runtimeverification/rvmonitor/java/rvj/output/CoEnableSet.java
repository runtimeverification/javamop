package com.runtimeverification.rvmonitor.java.rvj.output;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

public class CoEnableSet extends EnableSet {

    public CoEnableSet(List<EventDefinition> events,
            RVMParameters specParameters) {
        super(events, specParameters);
    }

    public CoEnableSet(String coenableSetStr, List<EventDefinition> events,
            RVMParameters specParameters) {
        super(coenableSetStr, events, specParameters);
    }

    public CoEnableSet(PropertyAndHandlers prop, List<EventDefinition> events,
            RVMParameters specParameters) {
        this(events, specParameters);
        for (String categoryName : prop.getHandlers().keySet()) {
            String coenableForCategory = prop.getLogicProperty(categoryName
                    .toLowerCase() + " coenables");

            if (coenableForCategory != null)
                add(new EnableSet(coenableForCategory, events, specParameters));
        }
    }

}
