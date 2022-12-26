package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.PTCARETParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.ParseException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.CodeGenVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.NumberingVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.SimplifyVisitor;

public class PTCARETPlugin extends LogicPlugin {
	
	public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
		//check if logic is ptcaret
		checkLogicEquals(logicInputXML, "ptcaret");
		
		// parse the formula and get the ast
		PTCARET_Formula ptCaRetformula;
		try {
			String logicStr = logicInputXML.getProperty().getFormula().trim();
			ptCaRetformula = PTCARETParser.parse(logicStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new LogicException(e.getMessage());
		}

		// simplify the formula
		ptCaRetformula = ptCaRetformula.accept(new SimplifyVisitor(), null);
		
		// number each sub-formula rooted in prev or since
		// It numbers concrete operators and abstract operators, separately.
		NumberingVisitor numberingVisitor = new NumberingVisitor();
		ptCaRetformula.accept(numberingVisitor, null);

		// generate pseudo-code
		Code code = ptCaRetformula.accept(new CodeGenVisitor(), null);
		
		// construct result xml
		LogicRepositoryType ret = logicInputXML;
		ret.getProperty().setLogic("ptcaret pseudo-code");
		ret.getProperty().setFormula(code.beforeCode + "output(" + code.output + ")\n" + code.afterCode);
		ret.getMessage().add("done");

		if(numberingVisitor.beta_counter != 0)
			ret.getMessage().add("versioned stack");

		return ret;
	}
	
	static protected PTCARETPlugin plugin = new PTCARETPlugin();
	
	public static void main(String[] args) {

		try {
			// Parse Input
			LogicRepositoryData logicInputData = new LogicRepositoryData(System.in);

			// use plugin main function
			if(plugin == null){
				throw new LogicException("Each plugin should initiate plugin field.");
			}
			LogicRepositoryType logicOutputXML = plugin.process(logicInputData.getXML());

			if (logicOutputXML == null) {
				throw new LogicException("no output from the plugin.");
			}

			ByteArrayOutputStream logicOutput = new LogicRepositoryData(logicOutputXML).getOutputStream();
			System.out.println(new ByteArrayInputStream(logicOutput.toByteArray()));
		} catch (LogicException e) {
			System.out.println(e);
		}

	}
}
