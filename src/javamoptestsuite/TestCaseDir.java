package javamoptestsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TestCaseDir {
	String basepath;
	String path;

	ArrayList<TestCaseDir> subTestCaseDirs = new ArrayList<TestCaseDir>();

	TestCase testCase = null;
	
	public TestCaseDir(String basepath) throws Exception{
		this(basepath, "");
	}

	public TestCaseDir(String basepath, String path) throws Exception{
		this.basepath = basepath;
		this.path = path;
		
		File directory = new File(basepath + "/" + path);
		if (!directory.exists())
			return;

		ArrayList<String> specFiles = collectSpecFiles(directory);

		if(specFiles.size() > 0){
			//this directory is only for a single test case.
			subTestCaseDirs.clear();
			this.testCase = new TestCase(basepath, path);
		} else {
			//this directory is for multiple test cases. And itself is not a test case.
			this.testCase = null;
			this.subTestCaseDirs = collectSubDirs(directory);
		}		
	}
	
	private ArrayList<String> collectSpecFiles(File directory){
		ArrayList<String> ret = new ArrayList<String>();
		
		for (File file : directory.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".mop")) {
					String name = file.getName();
					ret.add(name);
				}
			}
		}
		return ret;
	}

	private ArrayList<TestCaseDir> collectSubDirs(File directory) throws Exception{
		ArrayList<TestCaseDir> ret = new ArrayList<TestCaseDir>();
		
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				TestCaseDir subDir = new TestCaseDir(basepath, path + "/" + file.getName());
				ret.add(subDir);
			}
		}
		return ret;
	}
	
	public int numTestCasesOfStatus(boolean status){
		if(this.testCase != null){
			if(this.testCase.statusOK == status)
				return 1;
			else
				return 0;
		} else{
			int total = 0;
			for (TestCaseDir subTestCaseDir : this.subTestCaseDirs) {
				total += subTestCaseDir.numTestCasesOfStatus(status);
			}
			return total;
		}
	}
	
	
}
