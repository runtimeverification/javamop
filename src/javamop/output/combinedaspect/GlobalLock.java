package javamop.output.combinedaspect;

import javamop.output.MOPVariable;

public class GlobalLock {
	MOPVariable lock;

	public GlobalLock(MOPVariable lock) {
		this.lock = lock;
	}

	public MOPVariable getName(){
		return lock;
	}
	
	public String toString() {
		String ret = "";

		ret += "static Object " + lock + " = new Object();\n";

		return ret;
	}

}
