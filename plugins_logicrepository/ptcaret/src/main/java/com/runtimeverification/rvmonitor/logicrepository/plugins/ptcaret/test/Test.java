package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.test;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.Code;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.PTCARETParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.ParseException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.CodeGenVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.NumberingVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.SimplifyVisitor;

public class Test {

	
	public static void main(String[] args){
		String logicStr = "a S (b Sa (*) a)";
//		String logicStr = "!!!(!a && (b || c))";
//		String logicStr = "! a";
		PTCARET_Formula ptCaRetformula;
		
		try {
			ptCaRetformula = PTCARETParser.parse(logicStr);
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
