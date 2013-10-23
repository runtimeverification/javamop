package javamoptestsuite;

import java.io.File;
import java.util.*;

import javamop.util.StreamGobbler;

public class AspectJCompileFunctor implements TestCaseFunctor {
	public FunctorResult apply(TestCase testCase) {
		FunctorResult ret = new FunctorResult();

		// it does not care whether ProgramCompileFunctor succeed or not.
		// it just compiles the entire directory

		String os = System.getProperty("os.name");

		for (TestCaseProgDir testCaseProg : testCase.testing_programs) {
			ret.addSubCase(testCaseProg.dirName);

			String[] cmdarray;

			if (os.toLowerCase().contains("windows")) {
				String ajcrtlibPath = "";
				if (Main.ajcPath.compareTo("ajc") == 0) {
					Main.ajcPath = "ajc.bat";
				} else if (Main.ajcPath.compareTo("ajc.bat") != 0) {
					ajcrtlibPath = new File(new File(Main.ajcPath).getParent()).getParent() + "\\lib\\aspectjrt.jar";
				}

				String javamoprtLibPath = null;
				if (Main.isJarFile)
					javamoprtLibPath = new File(Main.jarFilePath).getParent() + "\\rt.jar";
				else
					javamoprtLibPath = Main.javamopDir + "\\lib\\rt.jar";

				
				String origDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName;
				String monitoredDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName
						+ "_MOP";

				String classpath = System.getenv("CLASSPATH");

				if (classpath == null || classpath.length() == 0) {
					classpath = "";
				}

				String[] cmdarray2 = { Main.ajcPath, "-1.5", "-cp",
						"\".;" + classpath + ";" + origDirPath + ";" + ajcrtlibPath + ";" + javamoprtLibPath + "\"", "-showWeaveInfo",
						"-inpath", origDirPath, "-d", monitoredDirPath };
				cmdarray = new String[cmdarray2.length + testCase.specFiles.size()];

				for (int i = 0; i < cmdarray2.length; i++) {
					cmdarray[i] = cmdarray2[i];
				}
				for (int i = 0; i < testCase.specFiles.size(); i++) {
					TestCaseSpec testCaseSpec = testCase.specFiles.get(i);
					cmdarray[cmdarray2.length + i] = testCase.basepath + File.separator + testCase.path + File.separator
							+ testCaseSpec.aspectj_filename;
				}
			} else {
				String ajcrtlibPath = "";
				if (Main.ajcPath.compareTo("ajc") != 0) {
					ajcrtlibPath = new File(new File(Main.ajcPath).getParent()).getParent() + "/lib/aspectjrt.jar";
				}

				String javamoprtLibPath = null;
				if (Main.isJarFile)
					javamoprtLibPath = new File(Main.jarFilePath).getParent() + "/rt.jar";
				else
					javamoprtLibPath = Main.javamopDir + "/lib/rt.jar";

				String origDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName;
				String monitoredDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName
						+ "_MOP";

				// Properties prop = System.getProperties();
				// String classpath = prop.getProperty("java.class.path",
				// null);
				String classpath = System.getenv("CLASSPATH");

				if (classpath == null || classpath.length() == 0) {
					classpath = "";
				}

				String[] cmdarray2 = { Main.ajcPath, "-1.5", "-cp",
						".:" + classpath + ":" + origDirPath + ":" + ajcrtlibPath + ":" + javamoprtLibPath, "-showWeaveInfo", "-inpath",
						origDirPath, "-d", monitoredDirPath };
				cmdarray = new String[cmdarray2.length + testCase.specFiles.size()];

				for (int i = 0; i < cmdarray2.length; i++) {
					cmdarray[i] = cmdarray2[i];
				}
				for (int i = 0; i < testCase.specFiles.size(); i++) {
					TestCaseSpec testCaseSpec = testCase.specFiles.get(i);
					cmdarray[cmdarray2.length + i] = testCase.basepath + File.separator + testCase.path + File.separator
							+ testCaseSpec.aspectj_filename;
				}
			}

			try {
				Process child;

				if (Main.Debug)
					System.out.println("AspectJCompile breakpoint 1");

				child = Runtime.getRuntime().exec(cmdarray, null);

				StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
				StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());

				if (Main.Debug)
					System.out.println("AspectJCompile breakpoint 2");

				outputGobbler.start();
				errorGobbler.start();

				//child.waitFor();

		 		outputGobbler.join();
				errorGobbler.join();

				if (Main.Debug)
					System.out.println("AspectJCompile breakpoint 3");

				ret.addStdOut(testCaseProg.dirName, outputGobbler.text);
				ret.addStdErr(testCaseProg.dirName, errorGobbler.text);

				if (Main.Debug)
					System.out.println("AspectJCompile breakpoint 4");
				
				if (outputGobbler.text.length() == 0) {
					ret.success = false;
					break;
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				ret.addStdErr(testCaseProg.dirName, "  " + e.getMessage());
				ret.success = false;
				break;
			}
		}

		return ret;

	}

}
