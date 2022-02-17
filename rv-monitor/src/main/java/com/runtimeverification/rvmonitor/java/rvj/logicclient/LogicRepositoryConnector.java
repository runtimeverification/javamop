package com.runtimeverification.rvmonitor.java.rvj.logicclient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.Formula;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.StreamGobbler;
import com.runtimeverification.rvmonitor.util.Tool;

public class LogicRepositoryConnector {
    public static boolean verbose = false;

    public static LogicRepositoryType process(RVMonitorSpec rvmSpec,
            PropertyAndHandlers prop) throws RVMException {
        if (rvmSpec == null || prop == null)
            throw new RVMException("No annotation specified");

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

        logicInputXML.setClient("RVMonitor");
        logicInputXML.setEvents(rvmSpec.getEventStr());
        logicInputXML.setCategories(categories);
        logicInputXML.setProperty(logicProperty);

        LogicRepositoryData logicInputData = new LogicRepositoryData(
                logicInputXML);

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
            throw new RVMException("Logic Engine Error: " + e.getMessage());
        }

        if (logicOutputXML.getProperty() == null) {
            if (logicOutputXML.getMessage() != null) {
                String errStr = "";
                for (String errMsg : logicOutputXML.getMessage()) {
                    if (errStr.length() != 0)
                        errStr += "\n";
                    errStr += errMsg.trim();
                }
                throw new RVMException(errStr);
            } else {
                throw new RVMException("Wrong Logic Repository Output");
            }
        }

        return logicOutputXML;
    }

    public static ByteArrayOutputStream connectToServer(
            LogicRepositoryData logicInputData) throws Exception {
        ByteArrayInputStream logicInput_InputStream = logicInputData
                .getInputStream();
        ByteArrayOutputStream logicInput_OutputStream = logicInputData
                .getOutputStream();
        String logicinputstr = logicInput_OutputStream.toString();

        if (verbose) {
            System.out.println("== send to logic repository ==");
            System.out.print(logicinputstr);
            System.out.println("");
        }

        ByteArrayOutputStream logicOutput_OutputStream;
        Class<?> logicClass = Class
                .forName("com.runtimeverification.rvmonitor.logicrepository.Main");
        ClassLoader loader = logicClass.getClassLoader();
        String logicClassPath = loader.getResource(
                "com/runtimeverification/rvmonitor/logicrepository/Main.class")
                .toString();

        boolean isLogicRepositoryInJar = false;
        String logicJarFilePath = "";
        String logicPackageFilePath = "";

        if (logicClassPath
                .endsWith(".jar!/com/runtimeverification/rvmonitor/logicrepository/Main.class")
                && logicClassPath.startsWith("jar:")) {
            isLogicRepositoryInJar = true;

            logicJarFilePath = logicClassPath
                    .substring(
                            "jar:file:".length(),
                            logicClassPath.length()
                                    - "!/com/runtimeverification/rvmonitor/logicrepository/Main.class"
                                            .length());
            logicJarFilePath = Tool.polishPath(logicJarFilePath);
        } else {
            logicPackageFilePath = logicClassPath.substring("file:".length(),
                    logicClassPath.length() - "/Main.class".length());
            logicPackageFilePath = Tool.polishPath(logicPackageFilePath);
        }

        String logicPluginFarFilePath = new File(logicJarFilePath).getParent()
                + File.separator + "plugins" + File.separator + "*";

        if (isLogicRepositoryInJar) {
            String mysqlConnectorPath = new File(Main.jarFilePath).getParent()
                    + "/lib/mysql-connector-java-3.0.9-stable-bin.jar";
            String executePath = new File(logicJarFilePath).getParent();

            String[] cmdarray = {
                    "java",
                    "-cp",
                    Tool.polishPath(logicJarFilePath) + File.pathSeparator
                            + logicPluginFarFilePath + File.pathSeparator
                            + mysqlConnectorPath + File.pathSeparator
                            + new File(Main.jarFilePath).getParent()
                            + "/scala-library.jar",
                    "com.runtimeverification.rvmonitor.logicrepository.Main" };

            logicOutput_OutputStream = executeProgram(cmdarray, executePath,
                    logicInput_InputStream);
        } else {
            // The following didn't work at least under Windows.
            // String executePath = new File(logicPackageFilePath).getParent();
            String executePath = null;
            {
                File logic = new File(logicPackageFilePath);
                File rvmonitor = logic.getParentFile();
                File runtimeverification = rvmonitor.getParentFile();
                File com = runtimeverification.getParentFile();
                File root = com.getParentFile();
                executePath = root.getAbsolutePath();
            }

            String mysqlConnectorPath = executePath + File.separator + "lib"
                    + File.separator
                    + "mysql-connector-java-3.0.9-stable-bin.jar";
            String scalaPath = executePath + File.separator + "lib"
                    + File.separator + "scala-library.jar";

            String[] cmdarray = {
                    "java",
                    "-cp",
                    Tool.polishPath(executePath) + File.pathSeparator
                            + mysqlConnectorPath + File.pathSeparator
                            + scalaPath,
            "com.runtimeverification.rvmonitor.logicrepository.Main" };

            logicOutput_OutputStream = executeProgram(cmdarray, executePath,
                    logicInput_InputStream);
        }

        if (verbose) {
            System.out.println("== result from logic repository ==");
            System.out.println(logicOutput_OutputStream);
            System.out.println("");
        }

        return logicOutput_OutputStream;
    }

    static public ByteArrayOutputStream executeProgram(String[] cmdarray,
            String path, ByteArrayInputStream input) throws RVMException {
        Process child;
        String output = "";
        try {
            child = Runtime.getRuntime().exec(cmdarray, null, new File(path));
            OutputStream out = child.getOutputStream();
            BufferedOutputStream bs = new BufferedOutputStream(out);

            StreamGobbler errorGobbler = new StreamGobbler(
                    child.getErrorStream());
            StreamGobbler outputGobbler = new StreamGobbler(
                    child.getInputStream());

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

            output = outputGobbler.getText() + errorGobbler.getText();

            ByteArrayOutputStream logicOutput = new ByteArrayOutputStream();
            logicOutput.write(output.getBytes());

            return logicOutput;
        } catch (Exception e) {
            System.out.println(e);
            if (cmdarray.length > 0)
                throw new RVMException("Cannot execute the program: "
                        + cmdarray[0]);
            else
                throw new RVMException("Cannot execute the program: ");
        }
    }

}
