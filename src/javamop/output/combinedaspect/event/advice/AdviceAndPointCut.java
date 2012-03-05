package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.MOPStatManager;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class AdviceAndPointCut {
	public MOPStatManager statManager;

	MOPVariable inlineFuncName;
	MOPParameters inlineParameters;

	MOPVariable pointcutName;
	public PointCut pointcut;
	MOPParameters parameters;

	boolean hasThisJoinPoint;
	public boolean isAround = false;
	public String retType;
	public String pos;
	public MOPParameters retVal;
	public MOPParameters throwVal;
	public MOPParameters threadVars = new MOPParameters();
	GlobalLock globalLock;
	boolean isSync;

	LinkedList<EventDefinition> events = new LinkedList<EventDefinition>();
	
	HashMap<EventDefinition, AdviceBody> advices = new HashMap<EventDefinition, AdviceBody>();

	MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");

	public AdviceAndPointCut(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		this.hasThisJoinPoint = mopSpec.hasThisJoinPoint();

		this.pointcutName = new MOPVariable(mopSpec.getName() + "_" + event.getUniqueId());
		this.inlineFuncName = new MOPVariable("MOPInline" + mopSpec.getName() + "_" + event.getUniqueId());
		this.parameters = event.getParametersWithoutThreadVar();
		this.inlineParameters = event.getMOPParametersWithoutThreadVar();

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
		
		this.globalLock = combinedAspect.lockManager.getLock();
		this.isSync = mopSpec.isSync();

		if(Main.scalable){
			if (mopSpec.isGeneral())
				this.advices.put(event, new ScalableGeneralAdviceBody(mopSpec, event, combinedAspect));
			else
				this.advices.put(event, new ScalableSpecialAdviceBody(mopSpec, event, combinedAspect));
		} else {
			if (mopSpec.isGeneral())
				this.advices.put(event, new GeneralAdviceBody(mopSpec, event, combinedAspect));
			else
				this.advices.put(event, new SpecialAdviceBody(mopSpec, event, combinedAspect));
		}
		
		this.events.add(event);
		
		this.pointcut = event.getPointCut();
	}

	public PointCut getPointCut() {
		return pointcut;
	}

	public boolean addEvent(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {

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
		if(Main.scalable){
			if (mopSpec.isGeneral())
				this.advices.put(event, new ScalableGeneralAdviceBody(mopSpec, event, combinedAspect));
			else
				this.advices.put(event, new ScalableSpecialAdviceBody(mopSpec, event, combinedAspect));
		} else {
			if (mopSpec.isGeneral())
				this.advices.put(event, new GeneralAdviceBody(mopSpec, event, combinedAspect));
			else
				this.advices.put(event, new SpecialAdviceBody(mopSpec, event, combinedAspect));
		}
		
		this.events.add(event);
		return true;
	}
	
	protected String adviceBody(){
		String ret = "";
		
		if(Main.empty_advicebody){
			ret += "System.out.print(\"\");\n";

			Iterator<EventDefinition> iter;
			if(this.pos.equals("before"))
				iter = this.events.descendingIterator();
			else
				iter = this.events.iterator();
			
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
			
			if (isSync)
				ret += "synchronized(" + globalLock.getName() + ") {\n";
	
			Iterator<EventDefinition> iter;
			if(this.pos.equals("before"))
				iter = this.events.descendingIterator();
			else
				iter = this.events.iterator();
			
			while(iter.hasNext()){
				EventDefinition event = iter.next(); 
						
				AdviceBody advice = advices.get(event);
	
				if(advices.size() > 1){
					ret += "//" + advice.mopSpec.getName() + "_" + event.getUniqueId() + "\n";
					ret += "{\n";
				}
				
				if (Main.statistics) {
					MOPStatistics stat = this.statManager.getStat(advice.mopSpec);
					
					ret += stat.eventInc(event.getId());
	
					for (MOPParameter param : event.getMOPParametersOnSpec()) {
						ret += stat.paramInc(param);
					}
	
					ret += "\n";
				}
	
				ret += advice;
				
				if(advices.size() > 1){
					ret += "}\n";
				}
			}
			
			if (isSync)
				ret += "}\n";

		}
		
		return ret;
	}

	public String toString() {
		String ret = "";
		String pointcutStr = pointcut.toString();

		if(Main.inline && !isAround){
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

		if(Main.inline && !isAround){
			ret += inlineFuncName + "(" + inlineParameters.parameterString();
			if(hasThisJoinPoint){
				if(inlineParameters.size() > 0) 
					ret += ", ";
				ret += "thisJoinPoint";
			}
			ret += ");\n";
		} else {
			ret += adviceBody();
		}

		ret += "}\n";

		return ret;
	}
}
