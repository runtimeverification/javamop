package javamop.parser.ast.mopspec;

import java.util.List;

import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.ast.type.Type;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;
import javamop.parser.main_parser.ParseException;

public class RVEventDefinition extends EventDefinition{

	public RVEventDefinition(int line, int column, String id, Type retType,
			String pos, List<MOPParameter> parameters, String pointCutStr,
			BlockStmt block, boolean hasReturning, List<MOPParameter> retVal,
			boolean hasThrowing, List<MOPParameter> throwVal, boolean startEvent)
			throws ParseException {
		super(line, column, id, retType, pos, parameters, pointCutStr, block,
				hasReturning, retVal, hasThrowing, throwVal, startEvent);
	}
	
	public RVEventDefinition(EventDefinition event) throws ParseException {
		super(0, 0, event.getId(), event.getRetType(), event.getPos(), event.parameters.toList(), event.getPointCutString(), 
				event.block, event.hasReturning, event.retVal.toList(), event.hasThrowing, event.throwVal.toList(), event.startEvent);
		
		// Add parameters in the returning and throwing pointcuts to the event definition
		if (this.hasReturning) {
			this.parameters.addAll(event.retVal.toList());
		}
		if (this.hasThrowing) {
			this.parameters.addAll(event.throwVal.toList());
		}
		
		//How to handle with start events?
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	@Override
	public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
		return v.visit(this, arg);
	}
}
