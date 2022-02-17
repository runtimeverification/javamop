/*
 * Main part of Logic Repository
 * 
 * author: Dongyun Jin
 * 
 */

package com.runtimeverification.rvmonitor.logicrepository;

import com.runtimeverification.rvmonitor.logicrepository.plugins.*;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.*;

import java.io.File;
import java.io.ByteArrayOutputStream;

public class Main {
	public static boolean Debug = true;
	public static boolean isJarFile = false;
	public static String jarFilePath = null;
	public static String basePath = null;

	/*
	 * public static String readInput() { Scanner scanner = new
	 * Scanner(System.in, "UTF-8"); String input = new String();
	 * 
	 * while (scanner.hasNextLine()) { input += scanner.nextLine() + "\n"; }
	 * return input; }
	 */

	// Read Logic Plugin Directory
	static public String readLogicPluginDir(String basePath) {
		String logicPluginDirPath = System.getenv("LOGICPLUGINPATH");
		if (logicPluginDirPath == null || logicPluginDirPath.length() == 0) {
			if (basePath.charAt(basePath.length() - 1) == '/')
				logicPluginDirPath = basePath + "plugins";
			else
				logicPluginDirPath = basePath + "/plugins";
		}

		return logicPluginDirPath;
	}

	// Polishing directory path for windows
	static public String polishPath(String path) {
		if (path.indexOf("%20") > 0)
			path = path.replaceAll("%20", " ");
		return path;
	}

	static public void main(String[] args) {
		int i = 0;

		while (i < args.length) {
			if (args[i].compareTo("-install") == 0) {
				i++;
				Statistics.install = true;
			}
			++i;
		}

		try {
			long startTime = System.currentTimeMillis();

			ClassLoader loader = Main.class.getClassLoader();
			String mainClassPath = loader.getResource("com/runtimeverification/rvmonitor/logicrepository/Main.class").toString();
			String logicRepositoryPath;
			if (mainClassPath.endsWith(".jar!/com/runtimeverification/rvmonitor/logicrepository/Main.class") && mainClassPath.startsWith("jar:")) {

				logicRepositoryPath = mainClassPath.substring("jar:file:".length(), mainClassPath.length()
						- "logic.repository.jar!/com/runtimeverification/rvmonitor/logicrepository/Main.class".length());
				logicRepositoryPath = polishPath(logicRepositoryPath);

				isJarFile = true;

				jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length()
						- "!/com/runtimeverification/rvmonitor/logicrepository/Main.class".length());
				jarFilePath = polishPath(jarFilePath);
			} else {
				logicRepositoryPath = Main.class.getResource(".").getFile();
			}

			basePath = logicRepositoryPath;

			// Initialize Logging
			Log.init(polishPath(basePath + "/log"));
			Log.setStatus(Log.SUCCESS);

			// Initialize Statistics
			if (!Statistics.init()) {
				Log.setStatus(Log.ERROR);
				Log.setErrorMsg("Database Connection Error");
			}
			// Read Logic Plugin Directory
			String logicPluginDirPath = polishPath(readLogicPluginDir(basePath));
			
			// Check if it exists
			File dirLogicPlugin = new File(logicPluginDirPath);
			if (!dirLogicPlugin.exists()) {
				Log.setStatus(Log.ERROR);
				Log
						.setErrorMsg("LOGICPLUGINPATH is not declared correctly. Also default plugins directory does not exist: "
								+ logicPluginDirPath);
				Log.flush();

				throw new LogicException(
						"LOGICPLUGINPATH is not declared correctly in the environment.\nAlso there is no default plugins directory:"
								+ logicPluginDirPath
								+ "\nPlease set LOGICPLUGINPATH to the logic plugin directory or place logic plugins in the default directory.");
			}

			// Parse Input
			LogicRepositoryData logicRepositoryData = new LogicRepositoryData(System.in);
			LogicRepositoryType logicXML = logicRepositoryData.getXML();

			// Get Logic Name and Client Name
			String logicName = null;
			if (logicXML.getProperty() != null)
				logicName = logicXML.getProperty().getLogic().toLowerCase();
			if (logicName == null || logicName.length() == 0) {
				Log.setStatus(Log.ERROR);
				Log.setErrorMsg("No logic names");
				Log.flush();
				throw new LogicException("no logic names");
			}
			String clientName = logicXML.getClient();
			if (clientName == null || clientName.length() == 0)
				clientName = "Anonymous Client";

			// Logging and Statistics
			Log.write("Client Name", clientName);
			Log.write("Logic Name", logicName);
			Log.write("Logic Repository Input", logicRepositoryData.getOutputStream().toString());
			Statistics.increase(clientName, logicName);

			// Find a logic plugin and apply
			ByteArrayOutputStream logicPluginResultStream = LogicPluginFactory.process(logicPluginDirPath, logicName,
					logicRepositoryData);

			// Error check
			if (logicPluginResultStream == null || logicPluginResultStream.size() == 0) {
				Log.setStatus(Log.ERROR);
				Log.setErrorMsg("Unknown Error from Logic Plugins");
				Log.flush();
				throw new LogicException("Unknown Error from Logic Plugins");
			}

			long endTime = System.currentTimeMillis();
			long runTime = endTime - startTime;

			// Add statistics
			LogicRepositoryData logicOutputData = new LogicRepositoryData(logicPluginResultStream);
			LogicRepositoryType logicOutputXML = logicOutputData.getXML();
			int clientAndLogicCount = Statistics.getClientAndLogicCount(clientName, logicName);
			int clientCount = Statistics.getClientCount(clientName);
			int logicCount = Statistics.getLogicCount(logicName);
			int totalCount = Statistics.getTotalCount();

			if (clientAndLogicCount != -1 && clientCount != -1 && logicCount != -1 && totalCount != -1) {
				StatisticsType logicStat = new StatisticsType();
				logicStat.setCurrentClient(clientName);
				logicStat.setCurrentLogic(logicName);
				logicStat.setClientAndLogicCount(clientAndLogicCount);
				logicStat.setClientCount(clientCount);
				logicStat.setLogicCount(logicCount);
				logicStat.setTotalRVMCount(totalCount);
				logicStat.setTotalExecutionTime(runTime + "ms");

				logicOutputXML.setStatistics(logicStat);
			}
			logicOutputData.updateXML();

			// Outputting
			String logicRepositoryOutput = logicOutputData.getOutputStream().toString();
			System.out.println(logicRepositoryOutput);

			// Logging
			Log.write("Logic Repository Output", logicRepositoryOutput);
			Log.setExecTime(runTime);
			Log.flush();
		} catch (LogicException e) {
			System.out.println(e);
		}

	}
}
