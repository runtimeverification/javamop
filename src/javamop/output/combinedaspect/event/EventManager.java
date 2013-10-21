package javamop.output.combinedaspect.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.event.advice.AdviceAndPointCut;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.visitor.ConvertPointcutToCNFVisitor;

public class EventManager {

	public ArrayList<AdviceAndPointCut> advices = new ArrayList<AdviceAndPointCut>();
	public ArrayList<EndObject> endObjectEvents = new ArrayList<EndObject>();
	public ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
	public ArrayList<StartThread> startThreadEvents = new ArrayList<StartThread>();
	public EndProgram endProgramEvent = null;

	public HashMap<JavaMOPSpec, MonitorSet> monitorSets;
	public HashMap<JavaMOPSpec, SuffixMonitor> monitors;
	public HashMap<JavaMOPSpec, EnableSet> enableSets;
	
	MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");

	public EventManager(String name, List<JavaMOPSpec> specs, CombinedAspect combinedAspect) throws MOPException {
		this.monitorSets = combinedAspect.monitorSets;
		this.monitors = combinedAspect.monitors;
		this.enableSets = combinedAspect.enableSets;

		this.endProgramEvent = new EndProgram(name);

		for (JavaMOPSpec spec : specs) {
			if (spec.isEnforce()) {
				endThreadEvents.add(new ThreadStatusMonitor(spec, combinedAspect));
			}
			for (EventDefinition event : spec.getEvents()) {
				// normal event
				if (!event.isEndObject() && !event.isEndProgram() && !event.isEndThread() && !event.isStartThread()) {
					boolean added = false;
					for (AdviceAndPointCut advice : advices) {
						if (advice.isAround != event.getPos().equals("around"))
							continue;
						if (advice.isAround) {
							if (!advice.retType.equals(event.getRetType().toString()))
								continue;
						}
						if (!advice.pos.equals(event.getPos()))
							continue;
						if (!advice.retVal.equals(event.getRetVal()))
							continue;
						if (!advice.throwVal.equals(event.getThrowVal()))
							continue;

						PointcutComparator comparator = new PointcutComparator();
						PointCut p1 = event.getPointCut().accept(new ConvertPointcutToCNFVisitor(), null);
						PointCut p2 = advice.getPointCut().accept(new ConvertPointcutToCNFVisitor(), null);
						
						if (comparator.compare(p1, p2)) {
							added = advice.addEvent(spec, event, combinedAspect);
							if(added)
								break;
						}
					}

					if (!added) {
						advices.add(new AdviceAndPointCut(spec, event, combinedAspect));
					}
				}

				// endObject
				if (event.isEndObject()) {
					endObjectEvents.add(new EndObject(spec, event, combinedAspect));
				}

				// endThread
				if (event.isEndThread()) {
					endThreadEvents.add(new EndThread(spec, event, combinedAspect));
				}

				// startThread
				if (event.isStartThread()) {
					startThreadEvents.add(new StartThread(spec, event, combinedAspect));
				}

				// endProgram
				if (event.isEndProgram()) {
					endProgramEvent.addEndProgramEvent(spec, event, combinedAspect);
				}

			} // end of for event

		} // end of for spec

		endProgramEvent.registerEndThreadEvents(endThreadEvents);

	}

	public MonitorSet getMonitorSet(JavaMOPSpec spec) {
		return monitorSets.get(spec);
	}

	public SuffixMonitor getMonitor(JavaMOPSpec spec) {
		return monitors.get(spec);
	}

	public EnableSet getEnableSet(JavaMOPSpec spec) {
		return enableSets.get(spec);
	}

	public String printConstructor() {
		String ret = "";

		if (endProgramEvent != null) {
			ret += endProgramEvent.printAddStatement();
		}

		return ret;
	}

	public String advices() {
		String ret = "";

		ret += "pointcut " + commonPointcut + "() : ";
		if(Main.dacapo){
			ret += "!within(javamoprt.MOPObject+) && !adviceexecution() && BaseAspect.notwithin();\n";
		} else if (Main.translate2RV) {
			
			ret += "!within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution();\n";
		}
		else {
			ret += "!within(javamoprt.MOPObject+) && !adviceexecution();\n";
		}
		
		int numAdvice = 1;
		advices = this.adjustAdviceOrder();
		for (AdviceAndPointCut advice : advices) {
			if(Main.empty_advicebody){
				ret += "// " + numAdvice++ + "\n";
			}
			
			if (Main.translate2RV) {
				ret += advice.toRVString();
			} else {
				ret += advice;
			}
			ret += "\n";
			if (advice.beCounted) {
				ret += "\n";
				ret += "// Declaration of the count variable for above pointcut\n";
				ret += "static int " + advice.getPointCutName() + "_count = 0;";
				ret += "\n\n\n";
			}
		}

		for (EndObject endObject : endObjectEvents) {
			ret += endObject;
			ret += "\n";
		}

		for (EndThread endThread : endThreadEvents) {
			ret += endThread.printAdvices();
			ret += "\n";
		}

		for (StartThread startThread : startThreadEvents) {
			ret += startThread.printAdvices();
			ret += "\n";
		}

		ret += endProgramEvent.printHookThread();

		return ret;
	} 
	
	/* 
	 * 
	 * Adjust advice order in the aspect so the before advice 
	 * comes before the after advices
	 * 
	 * **/
	private ArrayList<AdviceAndPointCut> adjustAdviceOrder() {
		ArrayList<AdviceAndPointCut> result = new ArrayList<AdviceAndPointCut>();
		for (AdviceAndPointCut advice : this.advices) {
			if (advice.pos.equals("before")) {
				result.add(0, advice);
			} else {
				result.add(advice);
			}
		}
		return result;
	}

	public static class EventMethodHelper {
		public static String methodName(String enclosingspec, EventDefinition event) {
			boolean mangle = false;
			if (Main.merge && Main.aspectname != null && Main.aspectname.length() > 0)
				mangle = true;
			
			StringBuilder s = new StringBuilder();
			if (mangle)
				s.append(Main.aspectname);
			else
				s.append(enclosingspec);
			s.append("RuntimeMonitor");
			s.append('.');
			if (mangle) {
				s.append(enclosingspec);
				s.append('_');
			}
			s.append(event.getId());
			s.append("Event");
			return s.toString();
		}

		public static String methodName(JavaMOPSpec enclosing, EventDefinition evt) {
			return methodName(enclosing.getName(), evt);
		}
	}
}
