package com.runtimeverification.rvmonitor.logicrepository.plugins.pda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.PDA;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.parser.PDAParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.parser.ParseException;

public class PDAPlugin extends LogicPlugin {
	
	public LogicRepositoryType process(LogicRepositoryType logicInputXML) throws LogicException {
		//check if logic is pda
		checkLogicEquals(logicInputXML, "pda");
		
		// parse the formula and get the ast
		PDA pda;
		try {
			String logicStr = logicInputXML.getProperty().getFormula().trim();
			pda = PDAParser.parse(logicStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new LogicException(e.getMessage());
		}

		//for debugging
		//System.out.println(pda.toString());
		
		// construct result xml
		LogicRepositoryType ret = logicInputXML;
		ret.getProperty().setLogic("pda");
		ret.getProperty().setFormula(pda.toString());
		ret.getMessage().add("done");

		return ret;
	}
	
	static protected PDAPlugin plugin = new PDAPlugin();
	
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
