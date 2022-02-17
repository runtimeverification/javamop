package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.Node;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;

public class PropertyAndHandlers extends Node {

    private final Property property;
    private final HashMap<String, String> handlers;

    // things that should be defined afterward
    int propertyId; // will be defined in RVMonitorSpec
    private Properties logicResult; // will be defined by RVMProcessor

    private final HashMap<String, String> eventMonitoringCodes = new HashMap<String, String>();
    private final HashMap<String, String> aftereventMonitoringCodes = new HashMap<String, String>();

    public PropertyAndHandlers(int line, int column, Property property,
            HashMap<String, String> handlers) {
        super(line, column);
        this.property = property;
        this.handlers = handlers;
    }

    public boolean hasHandler() {
        return (handlers != null);
    }

    public boolean hasGenericHandlers() {
        return (handlers != null);
    }

    public Property getProperty() {
        return property;
    }

    public HashMap<String, String> getHandlers() {
        return handlers;
    }

    public RVMParameters getUsedParametersIn(String category,
            RVMParameters specParam) {
        // All of them. If you want to have a property that doesn't use all the
        // parameters,
        // put it in another specification.
        return specParam;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setLogicShellOutput(LogicPluginShellResult logicShellOutput) {
        this.logicResult = logicShellOutput.properties;

        if (this.logicResult == null)
            return;

        parseMonitoredEvent(eventMonitoringCodes,
                this.logicResult.getProperty("monitored events"));
        parseMonitoredEvent(aftereventMonitoringCodes,
                this.logicResult.getProperty("after monitored events"));
    }

    public void parseMonitoredEvent(HashMap<String, String> codes,
            String eventStr) {
        if (eventStr == null)
            return;

        Pattern p = Pattern.compile("(\\!)?(\\#)?\\s*(\\w+):\\{\\n");
        Matcher matcher = p.matcher(eventStr);

        String eventName;
        String eventMonitoringCode;

        while (matcher.find()) {
            eventName = matcher.group();
            eventName = eventName.replaceAll("(\\!)?(\\#)?\\s*(\\w+):\\{\\n",
                    "$1$2$3");

            int k = com.runtimeverification.rvmonitor.util.Tool.findBlockEnd(
                    eventStr, matcher.end());

            String eventActionTemp = eventStr.substring(matcher.end(), k);
            String[] eventActionTemp2 = eventActionTemp.split("\n");

            eventMonitoringCode = "";
            for (String eventActionTemp3 : eventActionTemp2) {
                if (eventActionTemp3 != null && eventActionTemp3.length() != 0)
                    eventMonitoringCode += eventActionTemp3 + "\n";
            }
            codes.put(eventName, eventMonitoringCode);
        }
    }

    public String getLogicProperty(String input) {
        if (logicResult == null)
            return null;
        return logicResult.getProperty(input);
    }

    public String getEventMonitoringCode(String eventName) {
        return eventMonitoringCodes.get(eventName);
    }

    public String getAfterEventMonitoringCode(String eventName) {
        return aftereventMonitoringCodes.get(eventName);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
