package com.runtimeverification.rvmonitor.java.rvj.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

public class EnableSet {
    Map<String, RVMParameterSet> contents;
    protected final RVMParameters specParameters;
    protected final List<EventDefinition> events;

    HashMap<String, RVMParameters> parametersOnSpec = new HashMap<String, RVMParameters>();

    public EnableSet(List<EventDefinition> events, RVMParameters specParameters) {
        this.specParameters = specParameters;
        this.events = events;
        for (EventDefinition event : events) {
            parametersOnSpec.put(event.getId(), event.getRVMParametersOnSpec());
        }
        this.contents = new HashMap<String, RVMParameterSet>();
    }

    public EnableSet(String enableSetStr, List<EventDefinition> events,
            RVMParameters specParameters) {
        this(events, specParameters);
        this.contents = parseSets(enableSetStr);
    }

    public EnableSet(PropertyAndHandlers prop, List<EventDefinition> events,
            RVMParameters specParameters) {
        this(events, specParameters);
        for (String categoryName : prop.getHandlers().keySet()) {
            String enableForCategory = prop.getLogicProperty(categoryName
                    .toLowerCase() + " enables");

            if (enableForCategory != null)
                add(new EnableSet(enableForCategory, events, specParameters));
        }
    }

    public void add(EnableSet enableSet) {
        if (enableSet == null)
            return;

        for (Entry<String, RVMParameterSet> entry : enableSet.getEnables()) {
            RVMParameterSet enables = contents.get(entry.getKey());

            if (enables == null) {
                contents.put(entry.getKey(), entry.getValue());
            } else {
                enables.addAll(entry.getValue());
            }
        }
    }

    private Map<String, RVMParameterSet> parseSets(String logicResultEnableSets) {
        Map<String, RVMParameterSet> ret = new HashMap<String, RVMParameterSet>();

        String patternStr = "\\s*(\\w+)\\s*=\\s*\\[\\s*(\\[\\s*(\\w+\\s*(\\,\\s*\\w+\\s*)*)?\\s*\\](\\s*\\,\\s*\\[\\s*(\\w+\\s*(\\,\\s*\\w+\\s*)*)?\\s*\\])*)\\]";
        String patternStr2 = "\\[\\s*(\\w+\\s*(\\,\\s*\\w+\\s*)*)?\\s*\\]";

        Pattern p = Pattern.compile(patternStr);
        Matcher matcher = p.matcher(logicResultEnableSets);

        String eventName;
        String enableSetsStr;
        String aLine;

        while (matcher.find()) {
            aLine = matcher.group();

            eventName = aLine.replaceAll(patternStr, "$1");
            enableSetsStr = aLine.replaceAll(patternStr, "$2").replaceAll(
                    "\\n", "");

            RVMParameterSet enables = new RVMParameterSet();

            Pattern p2 = Pattern.compile(patternStr2);
            Matcher matcher2 = p2.matcher(enableSetsStr);

            while (matcher2.find()) {
                String aLine2 = matcher2.group();

                RVMParameters enableEntity = new RVMParameters();

                Pattern p3 = Pattern.compile("\\w+");
                Matcher matcher3 = p3.matcher(aLine2);

                while (matcher3.find()) {
                    String aLine3 = matcher3.group();

                    if (parametersOnSpec.get(aLine3.trim()) != null) {
                        enableEntity
                        .addAll(parametersOnSpec.get(aLine3.trim()));
                    }
                }

                enableEntity = specParameters.sortParam(enableEntity);

                enables.add(enableEntity);
            }

            enables.sort();

            ret.put(eventName, enables);
        }

        return ret;
    }

    public RVMParameterSet getEnable(String event) {
        if (contents.get(event) == null || Main.noopt1)
            return getFullEnable();
        return contents.get(event);
    }

    RVMParameterSet cachedFullEntity = null;

    public RVMParameterSet getFullEnable() {
        if (cachedFullEntity != null)
            return cachedFullEntity;

        RVMParameterSet fullEntity = new RVMParameterSet();

        boolean[] paramBool = new boolean[specParameters.size()];
        for (int i = 0; i < specParameters.size(); i++) {
            paramBool[i] = true;
        }

        while (true) {
            int i;
            RVMParameters entity = new RVMParameters();
            for (i = 0; i < specParameters.size(); i++) {
                if (paramBool[i] == true)
                    entity.add(specParameters.get(i));
            }
            fullEntity.add(entity);

            for (i = specParameters.size() - 1; i >= 0; i--) {
                if (paramBool[i] == true) {
                    paramBool[i] = false;
                    for (int j = i + 1; j < specParameters.size(); j++)
                        paramBool[j] = true;
                    break;
                }
            }
            if (i < 0)
                break;
        }

        fullEntity.sort();

        cachedFullEntity = fullEntity;

        return fullEntity;
    }

    public Set<Entry<String, RVMParameterSet>> getEnables() {
        return contents.entrySet();
    }

}
