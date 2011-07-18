package javamoptestsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TestCaseProgDir {
	TestCase testcase;

	String dirName;

	ArrayList<File> javaFiles = null;
	ArrayList<File> outputFiles = null;
	ArrayList<File> progNames = new ArrayList<File>();
		
	public TestCaseProgDir(TestCase parent, String dirName) throws Exception {
		this.testcase = parent;
		this.dirName = dirName;

		String testingProgPath = this.testcase.basepath + File.separator + this.testcase.path + File.separator + dirName;
		File directory = new File(testingProgPath);

		if (!directory.exists())
			throw new Exception("directory does not exist: " + testingProgPath);

		javaFiles = collectFiles(directory, "java");
		outputFiles = collectFiles(directory, "output");

		for (File outputFile : outputFiles) {
			if(!outputFile.getName().endsWith(".output"))
				throw new Exception("A file without extension .output is recognized as an output file, which should never happen");
			String outputFilePath = outputFile.getCanonicalPath();
			String classFilePath = outputFilePath.substring(0, outputFilePath.length() - 7);

			progNames.add(new File(classFilePath));
		}

	}

	private ArrayList<File> collectFiles(File directory, String ext) throws Exception {
		ArrayList<File> ret = new ArrayList<File>();
		ArrayList<File> directories = new ArrayList<File>();

		if (!directory.isDirectory())
			return ret;
		for (File file : directory.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().endsWith("." + ext)) {
					ret.add(file);
				}
			} else {
				directories.add(file);
			}
		}
		
		Collections.sort(ret, new Main.byNatural());
		Collections.sort(directories, new Main.byNatural());

		for (File file : directories) {
			if (!file.isDirectory()) {
				throw new Exception("[Error] Something strange happened.");
			} else {
				ArrayList<File> sub = collectFiles(file, ext);
				ret.addAll(sub);
			}
		}
		
		return ret;
	}

}