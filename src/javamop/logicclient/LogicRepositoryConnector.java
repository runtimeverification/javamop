package javamop.logicclient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javamop.Configuration;
import javamop.MOPException;
import javamop.Main;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.logicrepositorysyntax.LogicRepositoryType;
import javamop.parser.logicrepositorysyntax.PropertyType;
import javamop.util.StreamGobbler;
import javamop.util.Tool;

public class LogicRepositoryConnector {
	public static String serverName = "local";
	public static boolean verbose = false;

	public static LogicRepositoryType process(JavaMOPSpec mopSpec, PropertyAndHandlers prop) throws MOPException {
		if (mopSpec == null || prop == null)
			throw new MOPException("No annotation specified");

		String formula = "";
		String categories = "";

		formula += ((Formula) prop.getProperty()).getFormula().trim();
		for (String key : prop.getHandlers().keySet()) {
			categories += " " + key;
		}
		categories = categories.trim();

		LogicRepositoryType logicInputXML = new LogicRepositoryType();
		PropertyType logicProperty = new PropertyType();

		logicProperty.setFormula(formula);
		logicProperty.setLogic(prop.getProperty().getType());

		logicInputXML.setClient("JavaMOP");
		logicInputXML.setEvents(mopSpec.getEventStr());
		logicInputXML.setCategories(categories);
		logicInputXML.setProperty(logicProperty);

		LogicRepositoryData logicInputData = new LogicRepositoryData(logicInputXML);

		ByteArrayOutputStream logicOutput_OutputStream;
		LogicRepositoryData logicOutputData;
		LogicRepositoryType logicOutputXML;

		try {
			logicOutput_OutputStream = connectToServer(logicInputData);

			logicOutputData = new LogicRepositoryData(logicOutput_OutputStream);
			logicOutputXML = logicOutputData.getXML();
		} catch (Exception e) {
			if (Main.debug)
				e.printStackTrace();
			throw new MOPException("Logic Engine Error: " + e.getMessage());
		}

		if (logicOutputXML.getProperty() == null) {
			if (logicOutputXML.getMessage() != null) {
				String errStr = "";
				for (String errMsg : logicOutputXML.getMessage()) {
					if (errStr.length() != 0)
						errStr += "\n";
					errStr += errMsg.trim();
				}
				throw new MOPException(errStr);
			} else {
				throw new MOPException("Wrong Logic Repository Output");
			}
		}

		return logicOutputXML;
	}

	public static ByteArrayOutputStream connectToServer(LogicRepositoryData logicInputData) throws Exception {
		ByteArrayInputStream logicInput_InputStream = logicInputData.getInputStream();
		ByteArrayOutputStream logicInput_OutputStream = logicInputData.getOutputStream();
		String logicinputstr = logicInput_OutputStream.toString();

		if (verbose) {
			System.out.println("== send to logic repository ==");
			System.out.print(logicinputstr);
			System.out.println("");
		}

		ByteArrayOutputStream logicOutput_OutputStream;
		if (LogicRepositoryConnector.serverName.compareTo("local") == 0) {
			Class<?> logicClass = Class.forName("logicrepository.Main");
			ClassLoader loader = logicClass.getClassLoader();
			String logicClassPath = loader.getResource("logicrepository/Main.class").toString();

			boolean isLogicRepositoryInJar = false;
			String logicJarFilePath = "";
			String logicPackageFilePath = "";

			if (logicClassPath.endsWith(".jar!/logicrepository/Main.class") && logicClassPath.startsWith("jar:")) {
				isLogicRepositoryInJar = true;

				logicJarFilePath = logicClassPath.substring("jar:file:".length(), logicClassPath.length() - "!/logicrepository/Main.class".length());
				logicJarFilePath = Tool.polishPath(logicJarFilePath);
			} else {
				logicPackageFilePath = logicClassPath.substring("file:".length(), logicClassPath.length() - "/Main.class".length());
				logicPackageFilePath = Tool.polishPath(logicPackageFilePath);
			}

			String logicPluginFarFilePath = new File(logicJarFilePath).getParent() + File.separator + "plugins" + File.separator + "*";

			if (isLogicRepositoryInJar) {
				String mysqlConnectorPath = new File(Main.jarFilePath).getParent() + "/lib/mysql-connector-java-3.0.9-stable-bin.jar";
				String executePath = new File(logicJarFilePath).getParent();
				
				String[] cmdarray = {
						"java",
						"-cp",
						Tool.polishPath(logicJarFilePath) + File.pathSeparator + logicPluginFarFilePath + File.pathSeparator + mysqlConnectorPath
								+ File.pathSeparator + new File(Main.jarFilePath).getParent() + "/scala-library.jar", "logicrepository.Main" };

				logicOutput_OutputStream = executeProgram(cmdarray, executePath, logicInput_InputStream);
			} else {
				String executePath = new File(logicPackageFilePath).getParent();
				String mysqlConnectorPath = executePath + File.separator + "lib" + File.separator + "mysql-connector-java-3.0.9-stable-bin.jar";
				String scalaPath = executePath + File.separator + "lib" + File.separator + "scala-library.jar";

				String[] cmdarray = { "java", "-cp",
						Tool.polishPath(executePath) + File.pathSeparator + mysqlConnectorPath + File.pathSeparator + scalaPath, "logicrepository.Main" };

				logicOutput_OutputStream = executeProgram(cmdarray, executePath, logicInput_InputStream);
			}
		} else {
			URL url;
			URLConnection urlConn;
			DataOutputStream printout;
			BufferedReader input;
			String result = "";

			if (LogicRepositoryConnector.serverName.compareTo("default") == 0) {
				if(Configuration.getServerAddr() == null)
					throw new MOPException("remote server address configuration file not found");
				
				url = new URL(Configuration.getServerAddr());
			} else
				url = new URL(LogicRepositoryConnector.serverName);

			// URL connection channel.
			urlConn = url.openConnection();
			((HttpURLConnection) urlConn).setRequestMethod("POST");
			// Let the run-time system (RTS) know that we want input.
			urlConn.setDoInput(true);
			// Let the RTS know that we want to do output.
			urlConn.setDoOutput(true);
			// No caching, we want the real thing.
			urlConn.setUseCaches(false);
			// Specify the content type.
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// Send POST output.
			printout = new DataOutputStream(urlConn.getOutputStream());
			String request = URLEncoder.encode("input", "UTF-8") + "=" + URLEncoder.encode(logicinputstr, "UTF-8");
			printout.writeBytes(request);
			printout.flush();
			printout.close();

			// Get response data.
			input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String str;
			while (null != ((str = input.readLine()))) {
				result += str + "\n";
			}
			input.close();

			logicOutput_OutputStream = new ByteArrayOutputStream();
			logicOutput_OutputStream.write(result.getBytes());
		}

		if (verbose) {
			System.out.println("== result from logic repository ==");
			System.out.println(logicOutput_OutputStream);
			System.out.println("");
		}

		return logicOutput_OutputStream;
	}

	static public ByteArrayOutputStream executeProgram(String[] cmdarray, String path, ByteArrayInputStream input) throws MOPException {
		Process child;
		String output = "";
		try {
			child = Runtime.getRuntime().exec(cmdarray, null, new File(path));
			OutputStream out = child.getOutputStream();
			BufferedOutputStream bs = new BufferedOutputStream(out);

			StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
			StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());

			byte[] b = new byte[input.available()];
			input.read(b);

			bs.write(b);
			bs.flush();
			out.close();
			bs.close();

			errorGobbler.start();
			outputGobbler.start();

			child.waitFor();

			errorGobbler.join();
			outputGobbler.join();

			output = outputGobbler.text + errorGobbler.text;

			ByteArrayOutputStream logicOutput = new ByteArrayOutputStream();
			logicOutput.write(output.getBytes());

			return logicOutput;
		} catch (Exception e) {
			System.out.println(e);
			if (cmdarray.length > 0)
				throw new MOPException("Cannot execute the program: " + cmdarray[0]);
			else
				throw new MOPException("Cannot execute the program: ");
		}
	}

}
