package javamop.output.monitor;

import javamop.MOPException;
import javamop.output.OptimizedCoenableSet;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/***
 * 
 * Wrapper monitor class for enforcing properties.
 *
 */
public class EnforceMonitor extends BaseMonitor {

	public EnforceMonitor(String name, JavaMOPSpec mopSpec,
			OptimizedCoenableSet coenableSet, boolean isOutermost)
			throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost);
	}
	
	/***
	 * 
	 * Code to be inserted before the execution of a event. Used to enforce/avoid properties.
	 * 
	 * @return code used to control the execution of an event.
	 */
	@Override
	public String eventMethodPrefix() {
		return "System.out.println(\"Enforce event execution\");\n";
	}
	
	/***
	 * 
	 * Code to be inserted after the execution of a event. Used to notify all the other waiting threads.
	 * 
	 * @return code used to control the execution of an event.
	 */
	@Override
	public String eventMethodSuffix() {
		return "System.out.println(\"After enforce event execution\");\n";
	}
}
