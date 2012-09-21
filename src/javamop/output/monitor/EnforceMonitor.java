package javamop.output.monitor;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

/***
 * 
 * Wrapper monitor class for enforcing properties.
 *
 */
public class EnforceMonitor extends BaseMonitor {

	public EnforceMonitor(String name, JavaMOPSpec mopSpec,
			OptimizedCoenableSet coenableSet, boolean isOutermost)
			throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, "Enforcement");
		
	}
	
	/***
	 * 
	 * Code to be inserted before the execution of a event. Used to enforce/avoid properties.
	 * 
	 * @param prop property associated
	 * @return code used to control the execution of an event.
	 */
	@Override
	public String eventMethodPrefix(PropertyAndHandlers prop) {
		String ret = "";

		// Wait statement
		ret += "synchronized(this) { \n";
		ret += "try {\n";
		ret += "while (!this.shouldExecute()) {\n";
		ret += "this.wait();\n";
		ret += "}\n";
		ret += "} catch (InterruptedException e) {\n";
		ret += "e.printStackTrace();\n";
		ret += "}\n";
		ret += "}\n\n";
		
		return ret;
	}
	
	/***
	 * 
	 * Code to be inserted after the execution of a event. Used to notify all the other waiting threads.
	 * 
	 * @param prop property associated
	 * @return code used to control the execution of an event.
	 */
	@Override
	public String eventMethodSuffix(PropertyAndHandlers prop) {
		String ret = "";
		
		// notifyAll statement
		ret += "synchronized(this) { \n";
		ret += "this.notifyAll();\n";
		ret += "}\n";
		
		return ret;
	}
	
	@Override
	public String printExtraMethods() {
		String ret = "";
		ret += "public final boolean shouldExecute() { \n";
		ret += "return true;\n";
		ret += "}\n\n";
		return ret;
	}
}
