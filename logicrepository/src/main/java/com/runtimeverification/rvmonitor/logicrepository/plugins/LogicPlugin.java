package com.runtimeverification.rvmonitor.logicrepository.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;

public abstract class LogicPlugin {

	abstract public LogicRepositoryType process(LogicRepositoryType logicInput) throws LogicException;

	public boolean checkLogicEquals(LogicRepositoryType logicInputXML, String expectedLogic) throws LogicException{
		String logic = logicInputXML.getProperty().getLogic();
		logic = logic.toLowerCase();
		
		if (!logic.equals(expectedLogic.toLowerCase())) {
			throw new LogicException("incorrect logic type: " + logic);
		}

		return true;
	}
	
	public ByteArrayOutputStream process(
			ByteArrayInputStream logicPluginInputStream) throws LogicException {

		// Parse Input
		LogicRepositoryData logicData = new LogicRepositoryData(logicPluginInputStream);

		LogicRepositoryType logicOutputXML = process(logicData.getXML());

		ByteArrayOutputStream logicOutput = new LogicRepositoryData(logicOutputXML).getOutputStream();
		return logicOutput;
	}

	static protected LogicPlugin plugin = null;
	
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
