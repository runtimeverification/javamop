package com.runtimeverification.rvmonitor.logicrepository;

import java.io.File;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPlugin;
import com.runtimeverification.rvmonitor.logicrepository.plugins.LogicPluginFactory;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;

public class PluginHelper {
    
    /**
     * Run a LogicRepository plugin, retrieving the plugin from the standard plugin directory.
     * @param logicName The name of the LogicRepository plugin to run. Case-insensitive.
     * @param input The input data to run the LogicRepository plugin on.
     * @return The output of the given plugin on the given input.
     * @throws RuntimeException If the plugin cannot be found.
     */
    public static LogicRepositoryType runLogicPlugin(String logicName, LogicRepositoryType input) 
            throws LogicException {
        String path = System.getProperty("user.dir") + File.separator + "target" + File.separator +
            "classes" + File.separator + "com" + File.separator + "runtimeverification" +
            File.separator + "rvmonitor" + File.separator + "logicrepository" + File.separator + 
            "plugins";
        System.out.println(path);
        LogicPlugin plugin = LogicPluginFactory.findLogicPlugin(path, logicName);
        if(plugin == null) {
            throw new RuntimeException("No such plugin: " + logicName);
        }
        return plugin.process(input);
    }
}