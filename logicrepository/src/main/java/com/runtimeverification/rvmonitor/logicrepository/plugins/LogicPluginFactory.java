package com.runtimeverification.rvmonitor.logicrepository.plugins;

import java.io.*;
import java.util.jar.*;
import java.util.*;

import com.runtimeverification.rvmonitor.logicrepository.*;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.*;

public class LogicPluginFactory {

	static int numtry = 0;

	static public LogicPlugin findLogicPlugin(String logicPluginDirPath, String logicName) {
		String pluginName = logicName.toLowerCase() + "plugin";
		ArrayList<Class<?>> logicPlugins = null;
		try {
			/* it should return only subclasses of LogicPlugins */
			logicPlugins = getClassesFromPath(logicPluginDirPath);
			if (logicPlugins != null) {
				for (Class c : logicPlugins) {
					if (c.getSimpleName().toLowerCase().compareTo(pluginName) == 0) {
						LogicPlugin plugin = (LogicPlugin) (c.newInstance());
						return plugin;
					}
				}
			}
		} catch (Exception e) {
		  e.printStackTrace();
		  System.exit(1);
		}
		return null;
	}

	static public LogicPlugin findLogicPluginFromJar(String jarPath, String logicName) {
		if(jarPath == null)
			return null;
		String pluginName = logicName.toLowerCase() + "plugin";
		ArrayList<Class<?>> logicPlugins = null;
		try {
			/* it should return only subclasses of LogicPlugins */
			logicPlugins = getClassesFromJar(jarPath);

			if (logicPlugins != null) {
				for (Class c : logicPlugins) {
					if (c.getSimpleName().toLowerCase().compareTo(pluginName) == 0) {
						LogicPlugin plugin = (LogicPlugin) (c.newInstance());
						return plugin;
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	static private ArrayList<Class<?>> getClassesFromJar(String jarPath) throws LogicException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarPath));
			JarEntry jarEntry;

			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName().replaceAll("/", "\\.");
					className = className.substring(0, className.length() - ".class".length());
					Class<?> clazz;
					try {
						clazz =  Class.forName(className);
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
						continue;
					}
					if (!clazz.isInterface()) {
						Class<?> superClass = clazz.getSuperclass();
						while (superClass != null) {
							if (superClass.getName() == "com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin") {
								classes.add(clazz);
								break;
							}
							superClass = superClass.getSuperclass();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return classes;
	}
	
	static private ArrayList<Class<?>> getClassesFromPath(String packagePath) throws LogicException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		String path = packagePath;

		// WINDOWS HACK
		if (path.indexOf("%20") > 0)
			path = path.replaceAll("%20", " ");

		if (!(path.indexOf("!") > 0) && !(path.indexOf(".jar") > 0)) {
			try {
				classes.addAll(getFromDirectory(new File(path), "com.runtimeverification.rvmonitor.logicrepository.plugins"));
			} catch (Exception e) {
				throw new LogicException("cannot load logic plugins");
			}
		}
		return classes;
	}

	static private ArrayList<Class<?>> getFromDirectory(File directory, String packageName) throws Exception {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				if (file.getName().endsWith(".class")) {
					String name = packageName + '.' + stripFilenameExtension(file.getName());
					Class<?> clazz = null;
					try {
						clazz = Class.forName(name);
					} catch (Error e) {
						continue;
					}

					if (!clazz.isInterface()) {
						Class superClass = clazz.getSuperclass();
						while (superClass != null) {
							if (superClass.getName() == "com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin") {
								classes.add(clazz);
								break;
							}
							superClass = superClass.getSuperclass();
						}
					}
				} else if (file.list() != null) {
					classes.addAll(getFromDirectory(file, packageName + "." + stripFilenameExtension(file.getName())));
				}
			}
		}
		return classes;
	}

	static private String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(".");
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	static public ByteArrayOutputStream executeProgram(String[] cmdarray, String path, ByteArrayInputStream input)
			throws LogicException {
		Process child;
		String output = "";
		try {
			child = Runtime.getRuntime().exec(cmdarray, null, new File(path));
			OutputStream stdin = child.getOutputStream();

			StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
			StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());

			outputGobbler.start();
			errorGobbler.start();

			byte[] b = new byte[input.available()];
			input.read(b);

			stdin.write(b);
			stdin.flush();
			stdin.close();

			outputGobbler.join();
			errorGobbler.join();


			//child.waitFor();
			output = outputGobbler.getText() + errorGobbler.getText();

			ByteArrayOutputStream logicOutput = new ByteArrayOutputStream();
			logicOutput.write(output.getBytes());

			return logicOutput;
		} catch (Exception e) {
			if (cmdarray.length > 0)
				throw new LogicException("Cannot execute the logic plugin: " + cmdarray[0]);
			else
				throw new LogicException("Cannot execute the logic plugin: ");
		}
	}

	static public String[] getSuffixes() {
		String[] suffixes;
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			String[] suffixes_windows = { ".bat", ".exe", ".pl" };
			suffixes = suffixes_windows;
		} else {
			String[] suffxies_unix = { "", ".sh", ".pl" };
			suffixes = suffxies_unix;
		}

		return suffixes;
	}

	static public ByteArrayOutputStream process(String logicPluginDirPath, String logicName,
			LogicRepositoryData logicRepositoryData) throws LogicException {
		ByteArrayOutputStream ret = null;

		LogicPluginFactory.numtry++;
		Log.write(LogicPluginFactory.numtry + ". Logic Plugin Input to " + logicName, logicRepositoryData
				.getOutputStream().toString());

		// 1. LogicPlugin Class
		if (ret == null) {
			LogicPlugin plugin = findLogicPlugin(logicPluginDirPath, logicName);

			if (plugin != null)
				ret = plugin.process(logicRepositoryData.getInputStream());
		}

		// 2. LogicPlugin Class from Jar file
		if (ret == null) {
			boolean logicJarExists = false;
			File logicPluginDir = new File(logicPluginDirPath);
			String logicJarPath = null;
			if (logicPluginDir.exists()) {
				for (File file : logicPluginDir.listFiles()) {
					if (file.getName().toLowerCase().compareTo(logicName.toLowerCase() + ".jar") == 0) {
						if (file.exists() && !file.isDirectory()) {
							logicJarExists = true;
							logicJarPath = logicPluginDirPath + "/" + file.getName();
						}
					}
				}
			}
			LogicPlugin plugin = findLogicPluginFromJar(logicJarPath, logicName);

			if (plugin != null)
				ret = plugin.process(logicRepositoryData.getInputStream());
		}
		
		// 3. Programs ( <logicName> | <logicName>.exe | <logicName>.sh |
		// <logicName>.pl | <logicName>.bat )
		// Set Candidates for Program Names
		if (ret == null) {
			String[] suffixes = getSuffixes();
			String[] programNames = new String[suffixes.length];
			for (int i = 0; i < suffixes.length; i++) {
				programNames[i] = logicName + suffixes[i];
			}

			// Check if a dedicated directory exists
			boolean logicDirExists = false;
			File logicPluginDir = new File(logicPluginDirPath);
			File logicDir = null;
			if (logicPluginDir.exists()) {
				for (File file : logicPluginDir.listFiles()) {
					if (file.getName().toLowerCase().compareTo(logicName.toLowerCase()) == 0) {
						if (file.exists() && file.isDirectory()) {
							logicDirExists = true;
							logicDir = new File(logicPluginDirPath + "/" + file.getName());
						}
					}
				}
			}

			// Check if one of them exists and execute it
			if (logicDirExists) {
				for (String programName : programNames) {
					File pluginProgram = new File(logicDir + "/" + programName);
					if (pluginProgram.exists() && !pluginProgram.isDirectory() && pluginProgram.canExecute()) {
						// Found. Execute it
						String[] cmdarray = { pluginProgram.getAbsolutePath() };

						ret = executeProgram(cmdarray, logicDir.getAbsolutePath(), logicRepositoryData.getInputStream());
					}
				}
			}
		}

		// 4. anything else?

		// Transitive Processing
		if (ret != null) {
			LogicRepositoryData logicOutputData = new LogicRepositoryData(ret);
			LogicRepositoryType logicOutputXML = logicOutputData.getXML();

			Log.write(LogicPluginFactory.numtry + ". Logic Plugin Output from " + logicName, logicOutputData
					.getOutputStream().toString());

			boolean done = false;
			for (String msg : logicOutputXML.getMessage()) {
				if (msg.compareTo("done") == 0)
					done = true;
			}

			if (done) {
				return logicOutputData.getOutputStream();
			} else {
				if (logicOutputXML.getProperty() == null)
					throw new LogicException("Wrong Logic Plugin Result from " + logicName + " Logic Plugin");
				String logic = logicOutputXML.getProperty().getLogic();

				return process(logicPluginDirPath, logic, logicOutputData);
			}
		}

		Log.setStatus(Log.ERROR);
		Log.setErrorMsg("Logic Plugin Not Found");
		Log.flush();
		throw new LogicException("Logic Plugin Not Found");
	}

}
