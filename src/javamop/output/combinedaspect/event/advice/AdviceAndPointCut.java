package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class AdviceAndPointCut {

	MOPVariable pointcutName;
	public PointCut pointcut;
	MOPParameters parameters;

	public boolean isAround = false;
	public String retType;
	public String pos;
	public MOPParameters retVal;
	public MOPParameters throwVal;
	public MOPParameters threadVars = new MOPParameters();
	MOPStatistics stat;

	LinkedList<EventDefinition> events = new LinkedList<EventDefinition>();
	
	HashMap<EventDefinition, AdviceBody> advices = new HashMap<EventDefinition, AdviceBody>();

	public AdviceAndPointCut(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		this.pointcutName = new MOPVariable(mopSpec.getName() + "_" + event.getUniqueId());
		this.parameters = event.getParametersWithoutThreadVar();

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

		this.stat = combinedAspect.statManager.getStat(mopSpec);

		if (mopSpec.isGeneral())
			this.advices.put(event, new GeneralAdviceBody(mopSpec, event, combinedAspect));
		else
			this.advices.put(event, new SpecialAdviceBody(mopSpec, event, combinedAspect));
		
		this.events.add(event);
		
		this.pointcut = event.getPointCut();
	}

	public PointCut getPointCut() {
		return pointcut;
	}

	public void addEvent(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		parameters.addAll(event.getParametersWithoutThreadVar());

		// combine pointcuts

		// add an advice body.
		if (mopSpec.isGeneral())
			this.advices.put(event, new GeneralAdviceBody(mopSpec, event, combinedAspect));
		else
			this.advices.put(event, new SpecialAdviceBody(mopSpec, event, combinedAspect));
		
		this.events.add(event);
	}

	public String toString() {
		String ret = "";
		String pointcutStr = pointcut.toString();

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
		if(Main.dacapo){
			ret += "!within(javamoprt.MOPObject+) && !adviceexecution() && BaseAspect.notwithin();\n";
		} else {
			ret += "!within(javamoprt.MOPObject+) && !adviceexecution();\n";
		}

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

		for (MOPParameter threadVar : threadVars) {
			ret += "Thread " + threadVar.getName() + " = Thread.currentThread();\n";
		}

		Iterator<EventDefinition> iter;
		if(this.pos.equals("before"))
			iter = this.events.descendingIterator();
		else
			iter = this.events.iterator();
		
		while(iter.hasNext()){
			EventDefinition event = iter.next(); 
					
			AdviceBody advice = advices.get(event);

			if(advices.size() > 1){
				ret += "{\n";
			}
			
			if (Main.statistics) {
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

		ret += "}\n";

		return ret;
	}
}
