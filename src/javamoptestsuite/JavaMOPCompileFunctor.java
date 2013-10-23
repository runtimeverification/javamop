package javamoptestsuite;

import java.util.*;
import java.io.*;

import javamop.*;
import javamop.util.*;

public class JavaMOPCompileFunctor implements TestCaseFunctor {
	public FunctorResult apply(TestCase testCase) {
		FunctorResult ret = new FunctorResult();

		String server;
		if (Main.local)
			server = "-local";
		else
			server = "-remote";

		for (TestCaseSpec testCaseSpec : testCase.specFiles) {
			ret.addSubCase(testCaseSpec.name);

			String[] cmdarray = null;

			if (Main.isJarFile) {
				if (Main.local) {
					String logicRepositoryPath = new File(Main.jarFilePath).getParent() + File.separator + "logicrepository.jar";

					String[] cmdarray2 = { "java", "-cp", Main.jarFilePath + File.pathSeparator + logicRepositoryPath, "javamop.JavaMOPMain",
							server, "-v", "-debug", testCase.basepath + File.separator + testCase.path + File.separator + testCaseSpec.spec_filename };
					cmdarray = cmdarray2;
				} else {
					String[] cmdarray2 = { "java", "-cp", Main.jarFilePath, "javamop.JavaMOPMain", server, "-v", "-debug",
							testCase.basepath + File.separator + testCase.path + File.separator + testCaseSpec.spec_filename };
					cmdarray = cmdarray2;
				}
			} else {
				String[] cmdarray2 = { "java", "-cp", Main.javamopDir, "javamop.JavaMOPMain", server, "-v", "-debug",
						testCase.basepath + File.separator + testCase.path + File.separator + testCaseSpec.spec_filename };
				cmdarray = cmdarray2;
			}

			try {
				Process child;

				if (Main.Debug)
					System.out.println("JavaMOPCompile breakpoint 1");

				child = Runtime.getRuntime().exec(cmdarray, null);

				if (Main.Debug)
					System.out.println("JavaMOPCompile breakpoint 2");

				StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
				StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());

				errorGobbler.start();
				outputGobbler.start();

				//child.waitFor();

		 		outputGobbler.join();
				errorGobbler.join();


				if (Main.Debug)
					System.out.println("JavaMOPCompile breakpoint 3");

				ret.addStdOut(testCaseSpec.name, outputGobbler.text);
				ret.addStdErr(testCaseSpec.name, errorGobbler.text);

				if (Main.Debug)
					System.out.println("JavaMOPCompile breakpoint 4");

				if(testCase.testing_programs != null && testCase.testing_programs.size() != 0){
					if (outputGobbler.text.indexOf("generated") == -1) {
						ret.success = false;
					}
				} else {
					File errFile = new File(testCase.basepath + File.separator + testCase.path + File.separator + testCaseSpec.err_filename);
					
					if(errFile.exists()){ // this testcase must fail and generate the error in errFile
						String errmsg = Tool.convertFileToString(errFile);
						if(!errorGobbler.text.toLowerCase().contains(errmsg.toLowerCase())){
							ret.success = false;
						} else {
							testCase.doneTesting = true;
						}
					} else { // this testcase must succeed
						if (outputGobbler.text.indexOf("generated") == -1) {
							ret.success = false;
						} else {
							testCase.doneTesting = true;
						}
					}
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				ret.addStdErr(testCaseSpec.name, e.getMessage());
				ret.success = false;
			}
		}

		return ret;
	}
}
