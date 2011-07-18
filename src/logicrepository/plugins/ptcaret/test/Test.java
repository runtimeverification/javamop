package logicrepository.plugins.ptcaret.test;

import logicrepository.plugins.ptcaret.Code;
import logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import logicrepository.plugins.ptcaret.parser.PTCARET_Parser;
import logicrepository.plugins.ptcaret.parser.ParseException;
import logicrepository.plugins.ptcaret.visitor.CodeGenVisitor;
import logicrepository.plugins.ptcaret.visitor.NumberingVisitor;
import logicrepository.plugins.ptcaret.visitor.SimplifyVisitor;

public class Test {

	
	public static void main(String[] args){
		String logicStr = "a S (b Sa (*) a)";
//		String logicStr = "!!!(!a && (b || c))";
//		String logicStr = "! a";
		PTCARET_Formula ptCaRetformula;
		
		try {
			ptCaRetformula = PTCARET_Parser.parse(logicStr);
			System.out.println(ptCaRetformula);
			
			ptCaRetformula = ptCaRetformula.accept(new SimplifyVisitor(), null);
			System.out.println(ptCaRetformula);
			
			ptCaRetformula.accept(new NumberingVisitor(), null);
			
			Code code = ptCaRetformula.accept(new CodeGenVisitor(), null);
			
			System.out.print(code.beforeCode);
			System.out.println("output(" + code.output + ")");
			System.out.print(code.afterCode);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
}
