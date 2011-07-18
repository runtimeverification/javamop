package javamoptestsuite;

import java.util.*;

public class FunctorResult {

	public boolean success = true;

	ArrayList<String> subcases = new ArrayList<String>();
	HashMap<String, String> stdouts = new HashMap<String, String>();
	HashMap<String, String> stderrs = new HashMap<String, String>();

	public void addSubCase(String name){
		subcases.add(name);
	}
	
	public void addStdOut(String name, String text){ 
		stdouts.put(name, text);
	}
	
	public void addStdErr(String name, String text){ 
		stderrs.put(name, text);
	}
	
	
}
