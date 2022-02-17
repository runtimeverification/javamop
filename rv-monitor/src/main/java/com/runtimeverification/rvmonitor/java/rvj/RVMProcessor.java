/**
 * @author Feng Chen, Dongyun Jin
 * The class handling the mop specification tree
 */

package com.runtimeverification.rvmonitor.java.rvj;

import com.runtimeverification.rvmonitor.java.rvj.logicclient.LogicRepositoryConnector;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMOutputCode;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellFactory;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.Tool;

public class RVMProcessor {
    public static boolean verbose = false;

    public String name;

    public RVMProcessor(String name) {
        this.name = name;
    }

    private void registerUserVar(RVMonitorSpec rvmSpec) throws RVMException {
        for (EventDefinition event : rvmSpec.getEvents()) {
            RVMNameSpace.addUserVariable(event.getId());
            for (RVMParameter param : event.getRVMParameters()) {
                RVMNameSpace.addUserVariable(param.getName());
            }
        }
        for (RVMParameter param : rvmSpec.getParameters()) {
            RVMNameSpace.addUserVariable(param.getName());
        }
        RVMNameSpace.addUserVariable(rvmSpec.getName());
    }

    public String process(RVMSpecFile rvmSpecFile) throws RVMException {
        String result;

        // register all user variables to RVMNameSpace to avoid conflicts
        for (RVMonitorSpec rvmSpec : rvmSpecFile.getSpecs())
            registerUserVar(rvmSpec);

        // Connect to Logic Repository
        for (RVMonitorSpec rvmSpec : rvmSpecFile.getSpecs()) {
            for (PropertyAndHandlers prop : rvmSpec.getPropertiesAndHandlers()) {
                // connect to the logic repository and get the logic output
                LogicRepositoryType logicOutput = LogicRepositoryConnector
                        .process(rvmSpec, prop);
                // get the monitor from the logic shell
                LogicPluginShellResult logicShellOutput = LogicPluginShellFactory
                        .process(logicOutput, rvmSpec.getEventStr(), "java");
                prop.setLogicShellOutput(logicShellOutput);

                if (verbose) {
                    System.out.println("== result from logic shell ==");
                    System.out.print(logicShellOutput);
                    System.out.println("");
                }
            }
        }

        // Error Checker
        for (RVMonitorSpec rvmSpec : rvmSpecFile.getSpecs()) {
            RVMErrorChecker.verify(rvmSpec);
        }

        // Generate output code
        result = (new RVMOutputCode(name, rvmSpecFile)).toString();

        // Do indentation
        result = Tool.changeIndentation(result, "", "\t");

        return result;
    }

}
