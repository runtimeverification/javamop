package com.runtimeverification.rvmonitor.logicpluginshells;

import java.util.ArrayList;
import java.util.Properties;

public class LogicPluginShellResult {
    public Properties properties;
    public ArrayList<String> startEvents;

    @Override
    public String toString() {
        return "properties=" + properties + "\n" + "startEvents=" + startEvents;
    }
}
