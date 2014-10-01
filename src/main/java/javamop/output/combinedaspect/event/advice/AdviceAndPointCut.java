// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javamop.parser.MOPException;
import javamop.JavaMOPMain;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.ActivatorManager;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.MOPStatManager;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.output.combinedaspect.event.EventManager;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

/**
 * A pointcut for where to place code and advice code to place in it.
 */
public class AdviceAndPointCut {
    private final MOPStatManager statManager;
    private final ActivatorManager activatorsManager = null;
    
    private final MOPVariable inlineFuncName;
    private final MOPParameters inlineParameters;
    
    private final MOPVariable pointcutName;
    private final PointCut pointcut;
    private final MOPParameters parameters;
    private final String specName;
    private final String fileName;
    
    private final boolean hasThisJoinPoint;
    public boolean isAround = false;
    public boolean beCounted = false;
    public String retType;
    public final String pos;
    public final MOPParameters retVal;
    public final MOPParameters throwVal;
    private final MOPParameters threadVars = new MOPParameters();
    private final GlobalLock globalLock;
    private final boolean isSync;
    
    private final LinkedList<EventDefinition> events = new LinkedList<EventDefinition>();
    private final HashSet<JavaMOPSpec> specsForActivation = new HashSet<JavaMOPSpec>();
    private final HashSet<JavaMOPSpec> specsForChecking = new HashSet<JavaMOPSpec>();
    
    private final HashMap<EventDefinition, AdviceBody> advices = 
        new HashMap<EventDefinition, AdviceBody>();
    
    private final MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");
    
    private AroundAdviceLocalDecl aroundLocalDecl = null;
    private AroundAdviceReturn aroundAdviceReturn = null;

    /**
     * Construct the advice and pointcut for a specific event.
     * @param mopSpec The specification this is a part of.
     * @param event The event this advice is for.
     * @param combinedAspect The generated aspect that this is a part of.
     */
    public AdviceAndPointCut(final JavaMOPSpec mopSpec, final EventDefinition event, 
            final CombinedAspect combinedAspect) throws MOPException {
        this.hasThisJoinPoint = mopSpec.hasThisJoinPoint();
        
        this.specName = mopSpec.getName();
        this.pointcutName = new MOPVariable(mopSpec.getName() + "_" + event.getUniqueId());
        this.inlineFuncName = new MOPVariable("MOPInline" + mopSpec.getName() + "_" + 
            event.getUniqueId());
        this.parameters = event.getParametersWithoutThreadVar();
        this.inlineParameters = event.getMOPParametersWithoutThreadVar();
        this.fileName = combinedAspect.getFileName();

        if (event.getPos().equals("around")) {
            isAround = true;
            retType = event.getRetType().toString();
        }
        
        this.pos = event.getPos();
        this.retVal = event.getRetVal();
        this.throwVal = event.getThrowVal();
        
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            if (event.getParameters().getParam(event.getThreadVar()) == null)
                throw new MOPException("thread variable is not included in the event definition.");
            
            this.threadVars.add(event.getParameters().getParam(event.getThreadVar()));
        }
        
        this.statManager = combinedAspect.statManager;
        
        //this.activatorsManager = combinedAspect.activatorsManager;
        
        this.globalLock = combinedAspect.lockManager.getLock();
        this.isSync = mopSpec.isSync();
        
        this.advices.put(event, new AdviceBody(mopSpec, event, combinedAspect));
        
        this.events.add(event);
        if (event.getCountCond() != null && event.getCountCond().length() != 0) {
            this.beCounted = true;
        }
        
        this.pointcut = event.getPointCut();
        
        if (mopSpec.has__SKIP() || event.getPos().equals("around"))
            aroundLocalDecl = new AroundAdviceLocalDecl();
        if (event.getPos().equals("around"))
            aroundAdviceReturn = new AroundAdviceReturn(event.getRetType(),
                event.getParametersWithoutThreadVar());
        
        if(event.isStartEvent())
            specsForActivation.add(mopSpec);
        else
            specsForChecking.add(mopSpec);
    }
    
    /**
     * The pointcut object where the code is placed.
     * @return The pointcut.
     */
    public PointCut getPointCut() {
        return pointcut;
    }
    
    /**
     * The name of the pointcut object for the event.
     * @return The pointcut's name.
     */
    public String getPointCutName() {
        return pointcutName.getVarName();
    }
    
    /**
     * Add an additional event to be managed by this class.
     * @param mopSpec The specification of the new event.
     * @param event The new event to manage.
     * @param combinedAspect The generated aspect that includes the event.
     */
    public boolean addEvent(final JavaMOPSpec mopSpec, final EventDefinition event, 
            final CombinedAspect combinedAspect) throws MOPException {
        
        // Parameter Conflict Check
        for(MOPParameter param : event.getParametersWithoutThreadVar()){
            MOPParameter param2 = parameters.getParam(param.getName());
            
            if(param2 == null)
                continue;
            
            if(!param.getType().equals(param2.getType())){
                return false;
            }
        }
        
        parameters.addAll(event.getParametersWithoutThreadVar());
        
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            if (event.getParameters().getParam(event.getThreadVar()) == null)
                throw new MOPException("thread variable is not included in the event definition.");
            
            this.threadVars.add(event.getParameters().getParam(event.getThreadVar()));
        }
        
        // add an advice body.
        this.advices.put(event, new AdviceBody(mopSpec, event, combinedAspect));
        
        this.events.add(event);
        if (event.getCountCond() != null && event.getCountCond().length() != 0) {
            this.beCounted = true;
        }
        if(event.isStartEvent())
            specsForActivation.add(mopSpec);
        else
            specsForChecking.add(mopSpec);
        return true;
    }
    
    /**
     * The generated advice code for this event.
     * @return Generated advice code.
     */
    protected String adviceBody(){
        String ret = "";
        
        if(JavaMOPMain.empty_advicebody){
            ret += "System.out.print(\"\");\n";
            
            Iterator<EventDefinition> iter;
            if(this.pos.equals("before"))
                iter = this.events.descendingIterator();
            else
                iter = this.events.iterator();
            
            if (this.beCounted) {
                ret += "++" + this.pointcutName + "_count;\n";
            }
            
            while(iter.hasNext()){
                EventDefinition event = iter.next(); 
                
                AdviceBody advice = advices.get(event);
                
                if(advices.size() > 1){
                    ret += "//" + advice.mopSpec.getName() + "_" + event.getUniqueId() + "\n";
                }
            }
        } else {
            for (MOPParameter threadVar : threadVars) {
                ret += "Thread " + threadVar.getName() + " = Thread.currentThread();\n";
            }
            
            for(JavaMOPSpec spec : specsForActivation){
                ret += activatorsManager.getActivator(spec) + " = true;\n";
            }           
            
            if (isSync) {
                ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
                ret += "Thread.yield();\n";
                ret += "}\n";
            }
            
            Iterator<EventDefinition> iter;
            if(this.pos.equals("before"))
                iter = this.events.descendingIterator();
            else
                iter = this.events.iterator();
            
            if (this.beCounted) {
                ret += "++" + this.pointcutName + "_count;\n";
            }
            
            while(iter.hasNext()){
                EventDefinition event = iter.next(); 
                
                AdviceBody advice = advices.get(event);
                
                ret += this.statManager.incEvent(advice.mopSpec, event);
                
                if(specsForChecking.contains(advice.mopSpec)){
                    if(advices.size() > 1){
                        ret += "//" + advice.mopSpec.getName() + "_" + event.getUniqueId() + "\n";
                    }
                    
                    ret += "if (" + activatorsManager.getActivator(advice.mopSpec) + ") {\n";
                } else {
                    if(advices.size() > 1){
                        ret += "//" + advice.mopSpec.getName() + "_" + event.getUniqueId() + "\n";
                        ret += "{\n";
                    }
                }
                
                if (JavaMOPMain.options.statistics) {
                    MOPStatistics stat = this.statManager.getStat(advice.mopSpec);
                    
                    ret += stat.eventInc(event.getId());
                    
                    for (MOPParameter param : event.getMOPParametersOnSpec()) {
                        ret += stat.paramInc(param);
                    }
                    
                    ret += "\n";
                }
                
                // add check count condition here
                String countCond = event.getCountCond();
                
                if (countCond != null && countCond.length() != 0) {
                    countCond = countCond.replaceAll("count", this.pointcutName + "_count");
                    ret += "if (" + countCond + ") {\n";
                }
                ret += advice;
                
                if (countCond != null && countCond.length() != 0) {
                    ret += "}\n";
                }
                
                if(specsForChecking.contains(advice.mopSpec)){
                    ret += "}\n";
                } else {
                    if(advices.size() > 1){
                        ret += "}\n";
                    }
                }
            }
            
            if (isSync) {
                ret += globalLock.getName() + ".unlock();\n";
            }
            
        }
        
        return ret;
    }
    
    /**
     * Generated Java/AspectJ complete source code for this advice and pointcut as code that works
     * together with RV-Monitor generated code.
     * @return Java/AspectJ source code.
     */
    @Override
    public String toString() {
        String ret = "";
        String pointcutStr = pointcut.toString();
        
        // Do we need to handle inline?
        if(JavaMOPMain.inline && !isAround){
            ret += "void " + inlineFuncName + "(" + inlineParameters.parameterDeclString();
            if(hasThisJoinPoint){
                if(inlineParameters.size() > 0) 
                    ret += ", ";
                ret += "JoinPoint thisJoinPoint";
            }
            ret += ") {\n";
            
            ret += adviceBody();
            
            ret += "}\n";
        }
        
        
        ret += "pointcut " + pointcutName;
        ret += "(";
        ret += parameters.parameterDeclString();
        ret += ")";
        ret += " : ";
        if (pointcutStr != null && pointcutStr.length() != 0) {
            ret += "(";
            ret += pointcutStr;
            ret += ")";
            ret += " && ";
        }
        ret += commonPointcut + "();\n";
        
        if (isAround)
            ret += retType + " ";
        
        ret += pos + " (" + parameters.parameterDeclString() + ") ";
        
        if (retVal != null && retVal.size() > 0) {
            ret += "returning (";
            ret += retVal.parameterDeclString();
            ret += ") ";
        }
        
        if (throwVal != null && throwVal.size() > 0) {
            ret += "throwing (";
            ret += throwVal.parameterDeclString();
            ret += ") ";
        }
        
        ret += ": " + pointcutName + "(" + parameters.parameterString() + ") {\n";
        
        if (aroundLocalDecl != null)
            ret += aroundLocalDecl;
        
        if(JavaMOPMain.inline && !isAround){
            ret += inlineFuncName + "(" + inlineParameters.parameterString();
            if(hasThisJoinPoint){
                if(inlineParameters.size() > 0) 
                    ret += ", ";
                ret += "thisJoinPoint";
            }
            ret += ");\n";
        } else {
            
            // Call method here MOPNameRuntimeMonitor.nameEvent()
            // If there's thread var, replace with t (currentThread),
            // and also generate Thread t = currentThread before it
            // If there's return/ throw pointcut, cat in the end 
            
            for (MOPParameter threadVar : threadVars) {
                ret += "Thread " + threadVar.getName() + " = Thread.currentThread();\n";
            }
            
            Iterator<EventDefinition> iter;
            if(this.pos.equals("before"))
                iter = this.events.descendingIterator();
            else
                iter = this.events.iterator();
            
            while (iter.hasNext()) {
                EventDefinition event = iter.next();
                
                AdviceBody advice = advices.get(event);
                
                if (advices.size() > 1) {
                    ret += "//" + advice.mopSpec.getName() + "_"
                    + event.getUniqueId() + "\n";
                }
                
                String countCond = event.getCountCond();
                
                if (countCond != null && countCond.length() != 0) {
                    ret += "++" + this.pointcutName+ "_count;\n";
                    countCond = countCond.replaceAll("count", this.pointcutName
                    + "_count");
                    ret += "if (" + countCond + ") {\n";
                }
                
                ret += EventManager.EventMethodHelper.methodName(advice.mopSpec, event, 
                    this.fileName);
                ret += "(";
                
                // Parameters
                // Original (including threadVar)
                String  original = event.getParameters().parameterString();
                ret += original;
                
                // Parameters in returning pointcut
                if (event.getRetVal() != null && event.getRetVal().size() > 0) {
                    String retParameters = event.getRetVal().parameterString();
                    if (retParameters.length() > 0) {
                        if (original == null || original.length() == 0)
                        {
                            ret += retParameters;
                        } else {
                            ret += ", " + retParameters;
                        }
                    }
                }
                
                // Parameters in throwing pointcut
                if (event.getThrowVal() != null && event.getThrowVal().size() > 0) {
                    String throwParameters = event.getThrowVal().parameterString();
                    if (throwParameters.length() > 0) {
                        if (original == null || original.length() == 0)
                        {
                            ret += throwParameters;
                        } else {
                            ret += ", " + throwParameters;
                        }
                    }
                }
                
                // __STATICSIG should be passed as an argument because rv-monitor cannot infer
                if (event.has__STATICSIG()) {
                    String staticsig = "thisJoinPoint.getStaticPart().getSignature()";
                    if (original == null || original.length() == 0)
                    {
                        ret += staticsig;
                    } else {
                        ret += ", " + staticsig;
                    }
                }
                
                ret += ");\n";
                
                if (countCond != null && countCond.length() != 0) {
                    ret += "}\n";
                }
            }
        }
        
        if (aroundAdviceReturn != null)
            ret += aroundAdviceReturn;
        
        ret += "}\n";
        
        return ret;
    }
}
