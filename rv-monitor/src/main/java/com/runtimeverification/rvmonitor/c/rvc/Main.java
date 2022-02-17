/*
 * entry class for RVC - based somewhat off the MOP logic repository code,
 * but bare bones and easier to maintain
 *
 * author: Patrick Meredith
 *
 * previous author : Dongyun Jin
 */

package com.runtimeverification.rvmonitor.c.rvc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.runtimeverification.rvmonitor.core.parser.RVParser;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShell;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.CCFG;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.CFSM;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.CTFSM;
import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPluginFactory;
import com.runtimeverification.rvmonitor.util.FileUtils;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.Tool;

public class Main {

    /**
     * Generate C monitoring code for files passed on the command line.
     * 
     * @param args
     *            The command-line arguments to rv-monitor.
     */
    static public void main(String[] args) {
        try {
            String basePath = getBasePath();

            if (args.length < 1) {
                System.err
                        .println("usage is:  rv-monitor -c <spec_file>\n  Please specify a spec file");
                System.exit(1);
            }

            String logicPluginDirPath = Tool
                    .polishPath(readLogicPluginDir(basePath));

            File dirLogicPlugin = new File(logicPluginDirPath);

            if (!dirLogicPlugin.exists()) {
                throw new LogicException(
                        "Unrecoverable error: please place plugins in the default plugins directory:plugins");
            }

            boolean parametric = false;
            boolean llvm = false;
            for (int i = 0; i < args.length - 1; ++i) {
                if (args[i].equals("-p")) {
                    parametric = true;
                } else if (args[i].equals("-llvm")) {
                    llvm = true;
                }
            }
            CSpecification rvcParser = parseInput(args[args.length - 1]);

            // send Spec to logic repository to get the logic result
            LogicRepositoryData cmgDataOut = sendToLogicRepository(rvcParser,
                    logicPluginDirPath);

            // Outputting the logic result
            outputCode(cmgDataOut, rvcParser, parametric, llvm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the base path from which this class was invoked.
     * 
     * @return The location of the jar file or root of the project directory.
     */
    static private String getBasePath() {

        ClassLoader loader = Main.class.getClassLoader();
        String mainClassPath = loader.getResource(
                "com/runtimeverification/rvmonitor/c/rvc/Main.class")
                .toString();
        String cmgPath;
        if (mainClassPath
                .endsWith(".jar!/com/runtimeverification/rvmonitor/c/rvc/Main.class")
                && mainClassPath.startsWith("jar:")) {
            cmgPath = mainClassPath
                    .substring(
                            "jar:file:".length(),
                            mainClassPath.length()
                                    - "rv-monitor.jar!/com/runtimeverification/rvmonitor/c/rvc/Main.class"
                                            .length());
            cmgPath = Tool.polishPath(cmgPath);
        } else {
            cmgPath = Main.class.getResource(".").getFile();
        }
        return cmgPath;
    }

    /**
     * Parses rv-monitor C input and produces a CSpecification object from which
     * we can grab important data.
     * 
     * @param fileName
     *            The location of the C file to parse.
     * @return A CSpecification instance with extracted information from the C
     *         file.
     */
    static private CSpecification parseInput(String fileName)
            throws FileNotFoundException {
        return new RVParserAdapter(RVParser.parse(new InputStreamReader(
                new FileInputStream(fileName))));
    }

    /**
     * Generates the proper name for the logic plugin directory.
     * 
     * @param basePath
     *            The location of the project.
     * @return The location of the logic plugins.
     */
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

    /**
     * Send the specification to the logic repository. The appropriate logic
     * repository plugins are run on the code.
     * 
     * @param rvcParser
     *            Extracted information from the RVM C file being used.
     * @param logicPluginDirPath
     *            The location at which to find the logic repository plugins.
     * @return The output of the logic plugins.
     */
    static public LogicRepositoryData sendToLogicRepository(
            CSpecification rvcParser, String logicPluginDirPath)
            throws LogicException {
        LogicRepositoryType cmgXMLIn = new LogicRepositoryType();
        PropertyType logicProperty = new PropertyType();

        // Get Logic Name and Client Name
        String logicName = rvcParser.getFormalism();
        if (logicName == null || logicName.length() == 0) {
            throw new LogicException("no logic names");
        }

        cmgXMLIn.setSpecName(rvcParser.getSpecName());

        logicProperty.setFormula(rvcParser.getFormula());
        logicProperty.setLogic(logicName);

        cmgXMLIn.setClient("CMonGen");
        StringBuilder events = new StringBuilder();
        for (String event : rvcParser.getEvents().keySet()) {
            events.append(event);
            events.append(" ");
        }
        cmgXMLIn.setEvents(events.toString().trim());

        StringBuilder categories = new StringBuilder();
        for (String category : rvcParser.getHandlers().keySet()) {
            categories.append(category);
            categories.append(" ");
        }
        cmgXMLIn.setCategories(categories.toString().trim());

        PropertyType prop = new PropertyType();
        prop.setLogic(rvcParser.getFormalism());
        prop.setFormula(rvcParser.getFormula());

        cmgXMLIn.setProperty(prop);

        LogicRepositoryData cmgDataIn = new LogicRepositoryData(cmgXMLIn);

        // Find a logic plugin and apply it
        ByteArrayOutputStream logicPluginResultStream = LogicPluginFactory
                .process(logicPluginDirPath, logicName, cmgDataIn);

        // Error check
        if (logicPluginResultStream == null
                || logicPluginResultStream.size() == 0) {
            throw new LogicException("Unknown Error from Logic Plugins");
        }
        return new LogicRepositoryData(logicPluginResultStream);
    }

    /**
     * Evaluate the appropriate logic plugin shell on the logic formalism.
     * 
     * @param logicOutputXML
     *            The result of the logic repository plugins.
     * @param rvcParser
     *            The extracted information from the C file.
     * @return The result of applying the appropriate logic plugin shell to the
     *         parameters.
     * @throws LogicException
     *             Something went wrong in applying the logic plugin shell.
     */
    private static LogicPluginShellResult evaluateLogicPluginShell(
            LogicRepositoryType logicOutputXML, CSpecification rvcParser,
            boolean parametric) throws LogicException, RVMException {
        // TODO: make this reflective instead of using a switch over type
        String logic = logicOutputXML.getProperty().getLogic().toLowerCase();
        LogicPluginShell shell;

        if ("fsm".equals(logic)) {
            shell = new CFSM(rvcParser, parametric);
        } else if ("tfsm".equals(logic)) {
            shell = new CTFSM(rvcParser, parametric);
        } else if ("cfg".equals(logic)) {
            shell = new CCFG(rvcParser, parametric);
        } else {
            throw new LogicException(
                    "Only finite logics and CFG are currently supported");
        }

        return shell.process(logicOutputXML, logicOutputXML.getEvents());
    }

    /**
     * Output code for the monitor. Creates a C and a H file, optionally
     * compiles to LLVM.
     * 
     * @param cmgDataOut
     *            The output of the logic repository plugins.
     * @param rvcParser
     *            Extracted information from the RVM C file.
     */
    static private void outputCode(LogicRepositoryData cmgDataOut,
            CSpecification rvcParser, boolean parametric, boolean llvm)
            throws LogicException, RVMException, FileNotFoundException {
        LogicRepositoryType logicOutputXML = cmgDataOut.getXML();

        LogicPluginShellResult sr = evaluateLogicPluginShell(logicOutputXML,
                rvcParser, parametric);

        String rvcPrefix = (String) sr.properties.getProperty("rvcPrefix");
        String specName = (String) sr.properties.getProperty("specName");
        String constSpecName = (String) sr.properties.getProperty("constSpecName");

        String cFile = rvcPrefix + specName + "Monitor.c";
        String aFile = "aspect.map";
        String mFile = "Makefile.instrument";
        String mnFile = "Makefile.new";
        String hFile = rvcPrefix + specName + "Monitor.h";
        String bcFile = rvcPrefix + specName + "Monitor.bc";
        String hDef = rvcPrefix + constSpecName + "MONITOR_H";

        File cFileHandle = new File(cFile);
        FileOutputStream cfos = new FileOutputStream(cFileHandle);
        PrintStream cos = new PrintStream(cfos);

        if (!llvm) {
            FileOutputStream hfos = new FileOutputStream(new File(hFile));
            PrintStream hos = new PrintStream(hfos);
            hos.println("#ifndef " + hDef);
            hos.println("#define " + hDef);
            hos.println(sr.properties.getProperty("header declarations"));
            hos.println("#endif");
        }

        cos.println(rvcParser.getIncludes());
        cos.println("#include <stdlib.h>");
        cos.println(sr.properties.getProperty("state declaration"));
        cos.println(rvcParser.getDeclarations());
        cos.println(sr.properties.getProperty("categories"));
        cos.println(sr.properties.getProperty("reset"));
        cos.println(sr.properties.getProperty("monitoring body"));
        cos.println(sr.properties.getProperty("event functions"));
        /*
         * // Adding the aspect functions ArrayList<CSpecification.CutPoint>
         * cutpoints = rvcParser.getCutpoints(); if (!cutpoints.isEmpty()) {
         * File aFileHandle = new File(aFile); FileOutputStream afos = new
         * FileOutputStream(aFileHandle); PrintStream aos = new
         * PrintStream(afos); for (CSpecification.CutPoint cutpoint : cutpoints)
         * { String aspectFn = rvcPrefix + specName + cutpoint.eventName +
         * "_aspect"; //print aspect functions to .c file if (cutpoint.when ==
         * CSpecification.WhenType.INSTEAD) { cos.print(cutpoint.retType); }
         * else { cos.print("void"); } cos.print(" "); cos.print(aspectFn);
         * cos.print("("); if (cutpoint.when == CSpecification.WhenType.AFTER) {
         * cos.print(cutpoint.retType); cos.print(" "); cos.print(rvcPrefix +
         * "return"); if (!cutpoint.params.isEmpty()) cos.print(", "); }
         * cos.print(cutpoint.params); cos.print(") "); cos.println("{"); String
         * body = cutpoint.body; for (String name :
         * rvcParser.getEvents().keySet()) { body = body.replaceAll("@" + name,
         * rvcPrefix + specName + name); } body = body.replaceAll("@return",
         * rvcPrefix + "return"); cos.println(body); cos.println(rvcPrefix +
         * specName + cutpoint.eventName + "();"); cos.println("}"); //print
         * instrumentation code to aspect file switch(cutpoint.when) { case
         * BEFORE: aos.print("before "); break; case AFTER: aos.print("after ");
         * break; case INSTEAD: aos.print("instead of "); } switch
         * (cutpoint.what) { case EXEC: aos.print("executing "); break; case
         * CALL: aos.print("calling "); } aos.print(cutpoint.name);
         * aos.print(" call " ); aos.println(aspectFn); }
         * 
         * if (llvm) { outputLLVMMakefiles(mnFile, mFile, rvcPrefix, specName);
         * }
         * 
         * }
         */
        if (llvm) {
            outputLLVMBytecode(bcFile, cFile);
            cFileHandle.delete();
            System.out.println(bcFile + " file has been generated");
        } else {
            System.out
                    .println(cFile + " and " + hFile + " have been generated");
        }
    }

    /**
     * Output specialized Makefiles for compiling the generated LLVM code and
     * the monitored code together.
     * 
     * @param mnFile
     *            The filename for the new makefile.
     * @param mFile
     *            The filename for the instrumented makefile.
     * @param rvcPrefix
     *            The prefix used on monitoring code.
     * @param specName
     *            The name of the monitoring code.
     */
    private static void outputLLVMMakefiles(String mnFile, String mFile,
            String rvcPrefix, String specName) throws FileNotFoundException {
        File mnFileHandle = new File(mnFile);
        FileOutputStream mnfos = new FileOutputStream(mnFileHandle);
        PrintStream mnos = new PrintStream(mnfos);
        // The llvm backend assumes a Makefile.original exists building the
        // (unistrumented) project
        // and adds two other Makefiles:
        // * Makefile.instrument is used to instrument the .bc files with the
        // provided aspects
        // * Makefile.new is the new main Makefile, which defines tasks for
        // building and instrumenting and
        // delegates work to the other two

        // Makefile.new
        String nmakefile = FileUtils.extractFileFromJar(Main.class,
                "Makefile.new");

        mnos.print(nmakefile);
        File mFileHandle = new File(mFile);
        FileOutputStream mfos = new FileOutputStream(mFileHandle);
        PrintStream mos = new PrintStream(mfos);

        // Makefile.instrument
        String makefile = FileUtils.extractFileFromJar(Main.class,
                "Makefile.instrument");

        makefile = makefile.replaceAll(rvcPrefix + "_", rvcPrefix + specName);
        mos.print(makefile);
    }

    /**
     * Output the LLVM bytecode for the monitoring and monitored code.
     * 
     * @param bcFile
     *            The filename of the file to output the bytecode to.
     * @param cFile
     *            The filename of the generated C code.
     */
    private static void outputLLVMBytecode(String bcFile, String cFile)
            throws FileNotFoundException {
        try {
            Process p = Runtime.getRuntime().exec(
                    new String[] { "clang", "-emit-llvm", "-c", "-o", bcFile,
                            cFile });
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

}
