package javamop.parser.ast.visitor;

import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

/**
 * @author Qingzhou Luo
 */

public class RVDumpVisitor extends DumpVisitor {
	
	@Override
	public void visit(EventDefinition e, Object arg) {
		if (e.isCreationEvent()) {
			printer.print("creation ");
		}
		printer.print("event " + e.getId());
		MOPParameters parameters = e.getParameters();
		if (e.hasReturning()) {
			parameters.addAll(e.getRetVal().toList());
		}
		if (e.hasThrowing()) {
			parameters.addAll(e.getThrowVal().toList());
		}
		printSpecParameters(parameters, arg);
		if (e.getCondition() != null && e.getCondition().length() > 0) {
			printer.print("{\n");
			printer.print("if ( ! (" + e.getCondition() + ") ) {\n");
			printer.print("return;\n");
			printer.print("}\n");
		}
		
		if (e.getAction() != null) {
			e.getAction().accept(this, arg);
		}
		printer.printLn();
		if (e.getCondition() != null && e.getCondition().length() > 0) {
			printer.print("}\n");
		}
	}
}
