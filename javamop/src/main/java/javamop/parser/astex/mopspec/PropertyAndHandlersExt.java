// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.CollectMOPVarVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;

public class PropertyAndHandlersExt extends ExtNode {
    
    private final PropertyExt property;
    private final HashMap<String, BlockStmt> handlers;
    private final HashMap<String, MOPParameters> usedParameters = new HashMap<String, MOPParameters>();
    
    private final List<HandlerExt> handlerList;
    
    //things that should be defined afterward
    int propertyId; //will be defined in JavaMOPSpec
    Properties logicResult; //will be defined by MOPProcessor
    
    private final HashMap<String, String> eventMonitoringCodes = new HashMap<String, String>();
    private final HashMap<String, String> aftereventMonitoringCodes = new HashMap<String, String>();
    
    private boolean versionedStack = false;
    
    public PropertyAndHandlersExt(TokenRange tokenRange, PropertyExt property, HashMap<String, BlockStmt> handlers, List<HandlerExt> handlerList) {
        super(tokenRange);
        this.property = property;
        this.handlers = handlers;
        this.handlerList = handlerList;
    }
    
    public boolean hasHandler() {
        return (handlers != null);
    }
    
    public boolean hasGenericHandlers() {
        return (handlers != null);
    }
    
    public List<HandlerExt> getHandlerList(){
        return handlerList;
    }
    
    public PropertyExt getProperty() {
        return property;
    }
    
    public HashMap<String, BlockStmt> getHandlers() {
        return handlers;
    }
    
    public MOPParameters getUsedParametersIn(String category, MOPParameters specParam){
        MOPParameters ret;
        
        //if cached, return it.
        if((ret = usedParameters.get(category)) != null)
            return ret;
        
        BlockStmt handler = handlers.get(category);
        
        ret = handler.accept(new CollectMOPVarVisitor(), specParam);
        
        usedParameters.put(category, ret);
        
        return ret;
    }
    
    public int getPropertyId(){
        return propertyId;
    }
    
    public void parseMonitoredEvent(HashMap<String, String> codes, String eventStr){
        if(eventStr == null)
            return;
        
        Pattern p = Pattern.compile("(\\!)?(\\#)?\\s*(\\w+):\\{\\n");
        Matcher matcher = p.matcher(eventStr);
        
        String eventName;
        String eventMonitoringCode;
        
        while (matcher.find()) {
            eventName = matcher.group();
            eventName = eventName.replaceAll("(\\!)?(\\#)?\\s*(\\w+):\\{\\n", "$1$2$3");
            
            int k = javamop.util.Tool.findBlockEnd(eventStr, matcher.end());
            
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
    
    public String getLogicProperty(String input){
        if(logicResult == null)
            return null;
        return logicResult.getProperty(input);
    }
    
    public String getEventMonitoringCode(String eventName){
        return eventMonitoringCodes.get(eventName);
    }
    
    public String getAfterEventMonitoringCode(String eventName){
        return aftereventMonitoringCodes.get(eventName);
    }
    
    public void setVersionedStack(){
        versionedStack = true;
    }
    
    public boolean getVersionedStack(){
        return versionedStack;
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof javamop.parser.ast.visitor.MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
}
