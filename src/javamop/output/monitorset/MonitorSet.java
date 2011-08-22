package javamop.output.monitorset;

import java.util.ArrayList;
import java.util.List;

import javamop.output.MOPVariable;
import javamop.output.aspect.MOPStatistics;
import javamop.output.monitor.WrapperMonitor;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class MonitorSet {
	MOPVariable setName;
	MOPVariable monitorName;
	WrapperMonitor monitor;
	
	boolean extendedNode = false;
	
	ArrayList<EventDefinition> events;
	List<PropertyAndHandlers> properties;
	boolean has__LOC;
	boolean existSkip = false;
	
	MOPVariable loc = new MOPVariable("MOP_loc");
	MOPVariable skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");

	MOPStatistics stat;
	
	public MonitorSet(String name, JavaMOPSpec mopSpec, WrapperMonitor monitor, boolean extendedNode) {
		this.monitorName = monitor.getOutermostName();
		this.monitor = monitor;
		this.setName = new MOPVariable(monitorName + "_Set");
		this.events = new ArrayList<EventDefinition>(mopSpec.getEvents());
		this.properties = mopSpec.getPropertiesAndHandlers();
		this.extendedNode = extendedNode;
		
		this.has__LOC = mopSpec.has__LOC();
		
		for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
			for (BlockStmt handler : prop.getHandlers().values()) {
				if (handler.toString().indexOf("__SKIP") != -1){
					existSkip = true;
				}
			}
		}
		for (EventDefinition event : events) {
			if (event.has__SKIP()){
				existSkip = true;
				break;
			}
		}
		
		this.stat = new MOPStatistics(name, mopSpec);
	}

	public MonitorSet(String name, JavaMOPSpec mopSpec, WrapperMonitor monitor) {
		this(name, mopSpec, monitor, false);
	}

	public MOPVariable getName() {
		return setName;
	}

	public String getNode(MOPVariable monitorVar, MOPVariable monitorSetVar){
		String ret = "";
		
		ret += monitorVar + " = " + "((" + setName + ")" + monitorSetVar + ")" + ".node;\n";
		
		return ret;
	}
	
	public String setNode(String monitorSetVar, MOPVariable monitorVar){
		String ret = "";
		
		ret += "((" + setName + ")" + monitorSetVar + ")" + ".node" + " = " + monitorVar + ";\n";
		
		return ret;
	}
	
	public String addMonitor(String monitorSetVar, MOPVariable monitorVar){
		String ret = "";
		
		ret += monitorSetVar + ".add(" + monitorVar + ");\n";
		
		return ret;
	}
	
	public String Monitoring(MOPVariable monitorSetVar, EventDefinition event, MOPVariable loc){
		String ret = "";
		
		boolean isAround = event.getPos().equals("around");

		ret += "if (" + monitorSetVar + " != null) {\n";
		
		if (has__LOC) {
			if(loc != null)
				ret += monitorSetVar + "." + this.loc + " = " + loc + ";\n";
			else
				ret += monitorSetVar + "." + this.loc + " = " + "thisJoinPoint.getSourceLocation().toString()" + ";\n";
		}

		if (isAround && event.has__SKIP()) {
			ret += monitorSetVar + "." + skipAroundAdvice + " = false;\n";
		}

		ret += "((" + setName + ")" + monitorSetVar + ").event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";
		
		if (isAround && event.has__SKIP()) {
			ret += skipAroundAdvice + " |= " + monitorSetVar + "." + skipAroundAdvice + ";\n";
		}
		
		ret += "}\n";
		
		return ret;
	}

	public String toString() {
		String ret = "";

		MOPVariable monitor = new MOPVariable("monitor");
		MOPVariable num_terminated_monitors = new MOPVariable("num_terminated_monitors");
		MOPVariable i = new MOPVariable("i");
		// elementData and size are safe since they will be accessed by the prefix "this.".

		ret += "class " + setName + " implements javamoprt.MOPSet {\n";
		if(extendedNode)
			ret += "public " + monitorName + " node;\n";
		ret += "protected " + monitorName + "[] elementData;\n";
		ret += "public int size;\n";

		if (has__LOC)
			ret += "String " + loc + " = null;\n";
		
		if (existSkip){
			ret += "boolean " + skipAroundAdvice + " = false;\n";
		}

		ret += "\n";

		ret += "public " + setName + "(){\n";
		ret += "this.size = 0;\n";
		ret += "this.elementData = new " + monitorName + "[4];\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final int size(){\n";
		ret += "while(size > 0 && elementData[size-1].MOP_terminated) {\n";
		ret += "elementData[--size] = null;\n";
		ret += "}\n";
		ret += "return size;\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final boolean add(MOPMonitor e){\n";
		ret += "ensureCapacity();\n";
		ret += "elementData[size++] = (" + monitorName + ")e;\n";
		ret += "return true;\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final void endObject(int idnum){\n";
		ret += "for(int i = 0; i < size; i++){\n";
		ret += "MOPMonitor monitor = elementData[i];\n";
		ret += "if(!monitor.MOP_terminated){\n";
		ret += "monitor.endObject(idnum);\n";
		ret += "}\n";
		ret += "}\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final boolean alive(){\n";
		ret += "for(int i = 0; i < size; i++){\n";
		ret += "MOPMonitor monitor = elementData[i];\n";
		ret += "if(!monitor.MOP_terminated){\n";
		ret += "return true;\n";
		ret += "}\n";
		ret += "}\n";
		ret += "return false;\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final void endObjectAndClean(int idnum){\n";
		ret += "for(int i = size - 1; i > 0; i--){\n";
		ret += "MOPMonitor monitor = elementData[i];\n";
		ret += "if(monitor != null && !monitor.MOP_terminated){\n";
		ret += "monitor.endObject(idnum);\n";
		ret += "}\n";
		ret += "elementData[i] = null;\n";
		ret += "}\n";
		ret += "elementData = null;\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final void ensureCapacity() {\n";
		ret += "int oldCapacity = elementData.length;\n";
		ret += "if (size + 1 > oldCapacity) {\n";
		ret += "cleanup();\n";
		ret += "}\n";
		ret += "if (size + 1 > oldCapacity) {\n";
		ret += "Object oldData[] = elementData;\n";
		ret += "int newCapacity = (oldCapacity * 3) / 2 + 1;\n";
		ret += "if (newCapacity < size + 1){\n";
		ret += "newCapacity = size + 1;\n";
		ret += "}\n";
		ret += "elementData = Arrays.copyOf(elementData, newCapacity);\n";
		ret += "}\n";
		ret += "}\n";
		ret += "\n";

		ret += "public final void cleanup() {\n";
		ret += "int num_terminated_monitors = 0 ;\n";
		ret += "for(int i = 0; i + num_terminated_monitors < size; i ++){\n";
		ret += monitorName + " monitor = ";
		ret += "(" + monitorName + ")elementData[i + num_terminated_monitors];\n";
		ret += "if(monitor.MOP_terminated){\n";
		ret += "if(i + num_terminated_monitors + 1 < size){\n";
		ret += "do{\n";
		ret += "monitor = (" + monitorName + ")elementData[i + (++num_terminated_monitors)];\n";
		ret += "} while(monitor.MOP_terminated && i + num_terminated_monitors + 1 < size);\n";
		ret += "if(monitor.MOP_terminated){\n";
		ret += "num_terminated_monitors++;\n";
		ret += "break;\n";
		ret += "}\n";
		ret += "} else {\n";
		ret += "num_terminated_monitors++;\n";
		ret += "break;\n";
		ret += "}\n";
		ret += "}\n";
		ret += "if(num_terminated_monitors != 0){\n";
		ret += "elementData[i] = monitor;\n";
		ret += "}\n";
		ret += "}\n";
		ret += "if(num_terminated_monitors != 0){\n";
		ret += "size -= num_terminated_monitors;\n";
		ret += "for(int i = size; i < size + num_terminated_monitors ; i++){\n";
		ret += "elementData[i] = null;\n";
		ret += "}\n";
		ret += "}\n";
		ret += "}\n";

		for (EventDefinition event : this.events) {
			String eventName = event.getUniqueId();
			MOPParameters parameters = event.getMOPParameters();

			ret += "\n";

			ret += "public final void event_" + eventName + "(";
			ret += parameters.parameterDeclString();
			ret += ") {\n";

			ret += "int " + num_terminated_monitors + " = 0 ;\n";
			ret += "for(int " + i + " = 0; " + i + " + " + num_terminated_monitors + " < this.size; " + i + " ++){\n";
			ret += monitorName + " " + monitor + " = (" + monitorName + ")this.elementData[" + i + " + " + num_terminated_monitors + "];\n";
			ret += "if(" + monitor + ".MOP_terminated){\n";
			ret += "if(" + i + " + " + num_terminated_monitors + " + 1 < this.size){\n";
			ret += "do{\n";
			ret += monitor + " = (" + monitorName + ")this.elementData[" + i + " + (++" + num_terminated_monitors + ")];\n";
			ret += "} while(" + monitor + ".MOP_terminated && " + i + " + " + num_terminated_monitors + " + 1 < this.size);\n";
			ret += "if(" + monitor + ".MOP_terminated" + "){\n";
			ret += num_terminated_monitors + "++;\n";
			ret += "break;\n";
			ret += "}\n";
			ret += "} else {\n";
			ret += num_terminated_monitors + "++;\n";
			ret += "break;\n";
			ret += "}\n";
			ret += "}\n";
			
			ret += "if(" + num_terminated_monitors + " != 0){\n";
			ret += "this.elementData[" + i + "] = " + monitor + ";\n";
			ret += "}\n";

			ret += this.monitor.Monitoring(monitor, event, loc);
			
			ret += "}\n";

			ret += "if(" + num_terminated_monitors + " != 0){\n";
			ret += "this.size -= " + num_terminated_monitors + ";\n";
			ret += "for(int " + i + " = this.size;";
			ret += " " + i + " < this.size + " + num_terminated_monitors + ";";
			ret += " " + i + "++){\n";
			ret += "this.elementData[" + i + "] = null;\n";
			ret += "}\n";
			ret += "}\n";
			
			ret += "}\n";
		}

		ret += "}\n";

		return ret;
	}

}
