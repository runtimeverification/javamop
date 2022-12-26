package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.PTCARETParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.ParseException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.CodeGenVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.NumberingVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.SimplifyVisitor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * High-level tests of PTCaReT internals.
 * @author A. Cody Schuffelen
 */
public class PTCaretTest {
	
	/**
	 * Tests that PTCaReT can simplify complex boolean expressions.
	 */
	@Test
	public void testSimplify() throws ParseException {
		PTCARET_Formula ptCaretFormula = PTCARETParser.parse("!!!(!a && (b || c))");
		assertEquals("!!!(!a && (b || c))", ptCaretFormula.toString());
		
		ptCaretFormula = ptCaretFormula.accept(new SimplifyVisitor(), null);
		assertEquals("a || (!b && !c)", ptCaretFormula.toString());
	}
	
	/**
	 * Tests that PTCaReT can simplify and produce code for boolean expressions with temporal logic.
	 */
	@Test
	public void testSimplifyCode() throws ParseException {
		PTCARET_Formula ptCaretFormula = PTCARETParser.parse("a S (b Sa (*)a)");
		assertEquals("a S (b Sa (*)a)", ptCaretFormula.toString());
		
		ptCaretFormula = ptCaretFormula.accept(new SimplifyVisitor(), null);
		assertEquals("a S (b Sa (*)a)", ptCaretFormula.toString());
		
		ptCaretFormula.accept(new NumberingVisitor(), null);
		assertEquals("a S (b Sa (*)a)", ptCaretFormula.toString());
		
		Code code = ptCaretFormula.accept(new CodeGenVisitor(), null);
		assertEquals("a S (b Sa (*)a)", ptCaretFormula.toString());
		//This is pretty much regression testing, I don't know what this is supposed to do.
		assertEquals("$beta$[0] := ($alpha$[0] || b && $beta$[0]);\n$alpha$[1] := ($beta$[0] || a && $alpha$[1]);\n", code.beforeCode);
		assertEquals("$alpha$[0] := a;\n", code.afterCode);
		assertEquals("$alpha$[1]", code.output);
	}
}
