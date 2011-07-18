package javamop.logicpluginshells;

import java.util.*;

public class LogicPluginShellResult {
	public Properties properties;
	public ArrayList<String> startEvents;
	
	@Override
	public String toString() {
		return "properties=" + properties + "\n" + "startEvents=" + startEvents;
	}
}
