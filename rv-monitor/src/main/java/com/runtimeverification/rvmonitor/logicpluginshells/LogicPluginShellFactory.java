package com.runtimeverification.rvmonitor.logicpluginshells;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.logicpluginshells.cfg.JavaCFG;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.JavaFSM;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.JavaPDA;
import com.runtimeverification.rvmonitor.logicpluginshells.po.JavaPO;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.JavaPTCARET;
import com.runtimeverification.rvmonitor.logicpluginshells.srs.JavaSRS;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.JavaTFSM;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.util.RVMException;

public class LogicPluginShellFactory {

    static public LogicPluginShell findLogicShellPlugin(String monitorType) {
        switch (monitorType) {
            case "fsm":
                return new JavaFSM();
            case "cfg":
                return new JavaCFG();
            case "srs":
                return new JavaSRS();
            case "tfsm":
                return new JavaTFSM();
            case "pda":
                return new JavaPDA();
            case "po":
                return new JavaPO();
            case "ptcaret":
                return new JavaPTCARET();
            default:
                System.out.println("No plugin found for: " + monitorType);
                return null;
        }
    }

    static public LogicPluginShellResult process(LogicRepositoryType logicOutput, String events, String outputLanguage)
            throws RVMException {
        LogicPluginShell logicShellPlugin = findLogicShellPlugin(logicOutput.getProperty().getLogic());
//        System.out.println("LOGIC SHELL PLUGIN: " + logicShellPlugin.getClass().getName());
//        System.out.println("LOGIC SHELL LOGIC: " + logicOutput.getProperty().getLogic());
        if (logicShellPlugin != null) {
            LogicPluginShellResult result = logicShellPlugin.process(logicOutput, events);
            // Support deadlock detection since deadlock is not a state.
            result.properties.setProperty("deadlock condition", "");
            return result;
        } else
            return null;
    }
}
