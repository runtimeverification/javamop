package javamop.output.aspect;

import java.util.ArrayList;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.MOPVariable;
import javamop.output.aspect.advice.AdviceAndPointCut;
import javamop.output.aspect.indexingtree.IndexingDecl;
import javamop.output.aspect.specialevent.EndObject;
import javamop.output.aspect.specialevent.EndProgram;
import javamop.output.aspect.specialevent.EndThread;
import javamop.output.aspect.specialevent.StartThread;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class AspectBody {
	public GlobalLock globalLock;
	public IndexingDecl indexingDecl;
	public MonitorSet monitorSet;
	public WrapperMonitor monitor;
	public EnableSet enableSet;

	boolean isGeneral;
	boolean isSync;
	public MOPVariable timestamp;

	public MOPStatistics stat;

	public ArrayList<AdviceAndPointCut> advices = new ArrayList<AdviceAndPointCut>();
	public ArrayList<EndObject> endObjectEvents = new ArrayList<EndObject>();
	public ArrayList<EndProgram> endProgramEvents = new ArrayList<EndProgram>();
	public ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
	public ArrayList<StartThread> startThreadEvents = new ArrayList<StartThread>();

	//public MOPParameters endObjectParameters = new MOPParameters();
	
	public AspectBody(String name, JavaMOPSpec mopSpec, MonitorSet monitorSet, WrapperMonitor monitor, EnableSet enableSet)
			throws MOPException {
		this.monitorSet = monitorSet;
		this.monitor = monitor;
		this.enableSet = enableSet;

		this.globalLock = new GlobalLock(new MOPVariable(mopSpec.getName() + "_MOPLock"));
		this.timestamp = new MOPVariable(mopSpec.getName() + "_timestamp");

//		for (EventDefinition event : mopSpec.getEvents()) {
//			if (event.isEndObject() && event.getMOPParameters().size() != 0)
//				endObjectParameters.addAll(event.getMOPParameters());
//		}

		this.indexingDecl = new IndexingDecl(mopSpec, monitorSet, monitor, enableSet, this);

		this.isGeneral = mopSpec.isGeneral();
		this.isSync = mopSpec.isSync();

		this.stat = new MOPStatistics(name, mopSpec);

		for (EventDefinition event : mopSpec.getEvents()) {
			
			if (!event.isEndObject() && !event.isEndProgram() && !event.isEndThread() && !event.isStartThread())
				advices.add(new AdviceAndPointCut(mopSpec, event, this));
			
			if (event.isEndObject()) {
				endObjectEvents.add(new EndObject(mopSpec, event, this));
			}
			if (event.isEndThread()) {
				endThreadEvents.add(new EndThread(mopSpec, event, this));
			}
			if(event.isStartThread()){
				startThreadEvents.add(new StartThread(mopSpec, event, this));
			}
		}

		for (EventDefinition event : mopSpec.getEvents()) {
			if (event.isEndProgram()) {
				endProgramEvents.add(new EndProgram(mopSpec, event, endThreadEvents, this));
			}
		}

		if(endThreadEvents.size() > 0 && endProgramEvents.size() == 0)
			endProgramEvents.add(new EndProgram(mopSpec, endThreadEvents, this));
	}
	
	public String toString() {
		String ret = "";

		if (Main.statistics) {
			ret += stat.fieldDecl();
			ret += "\n";
		}

		if (isSync)
			ret += globalLock;

		ret += "\n";

		if (isGeneral) {
			ret += "static long " + timestamp + " = 1;\n";
			ret += "\n";
		}

		ret += indexingDecl;

		for (EndObject endObject : endObjectEvents) {
			ret += "\n";
			ret += endObject.printDecl();
		}

		for (AdviceAndPointCut advice : advices) {
			ret += "\n";
			ret += advice;
		}

		for (StartThread startThread : startThreadEvents) {
			ret += "\n";
			ret += startThread.printAdvices();
		}

		for (EndThread endThread : endThreadEvents) {
			ret += "\n";
			ret += endThread.printAdvices();
		}
		
		for (EndProgram endProgram : endProgramEvents) {
			ret += "\n";
			ret += endProgram.printHookThread();
		}

		if (Main.statistics) {
			ret += "\n";
			ret += stat.advice();
		}

		return ret;
	}
}
