package javamoptestsuite;

import java.io.*;
import java.util.*;

public class Main {
	static boolean Debug = false;

	static boolean verbose = false;
	static boolean verboseOnFailure = false;
	static String ajcPath = null;
	static String javamopDir = null;

	//all currently sold processors have at least two hardware threads
	//even if someone does not, running two threads won't hurt their
	//performance too much
    static int numThreads = 2;

	static String errMsg = "";

	static boolean local = false;

        static int numDots = 75;

	public static boolean isJarFile = false;
	public static String jarFilePath = null;

	public static String getName(String path) {
		if (path.endsWith(".mop")) {
			return path.substring(0, path.length() - 4);
		} else if (path.endsWith(".java")) {
			return path.substring(0, path.length() - 5);
		} else if (path.endsWith(".class")) {
			return path.substring(0, path.length() - 6);
		} else if (path.endsWith(".output")) {
			return path.substring(0, path.length() - 7);
		} else
			return path;
	}

	public static String resultString(String testName){
      int padding = numDots - testName.length();
	  String dots = "";
	  for(int i = 0; i < padding; ++i) dots += ".";
      return testName + dots;
	}

	public static String polishPath(String path) {
		if (path.indexOf("%20") > 0)
			path = path.replaceAll("%20", " ");

		return path;
	}

	public static void copy(String fromFileName, String toFileName) throws Exception {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("File Copy Error, " + fromFile + " does not exist");
		if (!fromFile.isFile())
			throw new IOException("File Copy Error, " + fromFile + " is not a file");
		if (!fromFile.canRead())
			throw new IOException("File Copy Error, " + fromFile + " is not readable");

		if (toFile.isDirectory())
			throw new IOException("File Copy Error, " + fromFile + " is a directory");

		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException("File Copy Error, " + fromFile + " is not writeable");
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("File Copy Error, " + dir + " does not exist");

			if (dir.isFile())
				throw new IOException("File Copy Error, " + dir + " is not a directory");
			if (!dir.canWrite())
				throw new IOException("File Copy Error, " + dir + " is not writeable");
		}

		FileInputStream from = null;
		FileOutputStream to = null;

		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}

	public static void print_help() {
		System.out.println("Usage: java [-cp javmaop_classpath] javamop.TestSuite [-options] <test suite dir>");
		System.out.println("");

		System.out.println("where options include:");
		System.out.println("    -h -help\t\t\t  print this help message");
		System.out.println("    -v | -verbose\t\t  enable verbose output");
		System.out.println("    -vf | -verboseonfail\t\t  enable verbose output for failed test cases");
		System.out.println("    -local\t\t\t  use local logic plugin");
		System.out.println("    -j n\t\t\t  use n threads, default is two");
		System.out.println();

		System.out.println("    -a <aspectj compiler path>\t  select the path of aspectj compiler");
		System.out.println("    -m <javamop dir path>\t  select the javamop directory");
		System.out.println();
	}

	public static class byNatural implements java.util.Comparator<File> {
		public int compare(File a, File b) {
			return compare(a.getName(), b.getName());
		}

		public int compare(String a, String b) {
			int sif = a.compareTo(b);

			String a1 = getName(a);
			String b1 = getName(b);

			String a2 = a1;
			String a3 = "";
			for (int i = a1.length() - 1; i >= 0; i--) {
				if (a1.charAt(i) >= '0' && a1.charAt(i) <= '9') {
					continue;
				} else {
					a2 = a1.substring(0, i + 1);
					a3 = a1.substring(i + 1, a1.length());
					break;
				}
			}

			String b2 = a1;
			String b3 = "";
			for (int i = b1.length() - 1; i >= 0; i--) {
				if (b1.charAt(i) >= '0' && b1.charAt(i) <= '9') {
					continue;
				} else {
					b2 = b1.substring(0, i + 1);
					b3 = b1.substring(i + 1, b1.length());
					break;
				}
			}

			if (a2.compareTo(b2) != 0 || a3.length() == 0 || b3.length() == 0)
				return sif;

			int siif = Integer.parseInt(a3) - Integer.parseInt(b3);
			return siif;
		}
	}

	static public boolean traverseTestCaseDir(TestCaseDir testCaseDir, TestCaseFunctor functor) throws Exception {
		BooleanRef ret = new BooleanRef(true);

		if (testCaseDir.testCase != null) {
			if (!testCaseDir.testCase.statusOK || testCaseDir.testCase.doneTesting)
				return true;

			FunctorResult result = functor.apply(testCaseDir.testCase);
			
			synchronized(ret){
			  ret.toBool = ret.toBool && result.success;
			}

			if (!result.success)
				testCaseDir.testCase.statusOK = false;

			if (Main.verbose || (!result.success && Main.verboseOnFailure)) {
			  synchronized(System.out){
				for (String subCaseName : result.subcases) {
					System.out.println("** " + testCaseDir.testCase.path + File.separator + subCaseName);

					String output = result.stdouts.get(subCaseName);
					if (output != null && output.length() != 0) {
						System.out.println("  ==output from " + testCaseDir.testCase.path + File.separator + subCaseName + "==");
						System.out.println(output);
					}

					String error = result.stderrs.get(subCaseName);
					if (error != null && error.length() != 0) {
						System.out.println("  ==error from " + testCaseDir.testCase.path + File.separator + subCaseName + "==");
						System.out.println(error);
					}

					if ((output != null && output.length() != 0) || (error != null && error.length() != 0))
						System.out.println("  ====");
				}
				if (result.success)
					System.out.println(testCaseDir.testCase.path + "\n[[[OK]]]");
				else
					System.out.println(testCaseDir.testCase.path + "\n[[[Fail]]]");
			  }
			} else {
				if (result.success)
					System.out.println(resultString(testCaseDir.testCase.path) + "[OK]");
				else
					System.out.println(resultString(testCaseDir.testCase.path) + "[Fail]");
			}
		

		} else if (testCaseDir.subTestCaseDirs != null && testCaseDir.subTestCaseDirs.size() != 0) {
			Iterator<TestCaseDir> dirIterator = testCaseDir.subTestCaseDirs.iterator();
			List<Thread> threads = new ArrayList<Thread>(Main.numThreads);
			BooleanRef retRef = new BooleanRef(true);
			while(dirIterator.hasNext()){
                threads.clear();
				for(int i = 0; i < numThreads; ++i){
				  if(!dirIterator.hasNext()) break;
                  threads.add(new Thread(new TraverseTask(ret, dirIterator.next(), functor)));
				}
                for(Thread t : threads) t.start();
				try { for(Thread t : threads) t.join(); } 
				catch(Exception e) { e.printStackTrace(); }
			}
		}

		return ret.toBool;
	}

	public static String repeatChar(char a, int numTimes) {
		StringBuffer p = new StringBuffer();
		for (int i = 0; i < numTimes; i++)
			p.append(a);
		return p.toString();
	}

	/*
	 * 
	 * testSuite Dir Path is either of a testcase directory or a collection
	 * containing several testcases.
	 */
	static public boolean process(String testSuiteDirPath) {
	   	boolean success = false;
		// TestCaseDir rootDir = getTestCaseDir(testSuiteDirPath, "");

		TestCaseDir rootDir = null;
		try {
			rootDir = new TestCaseDir(testSuiteDirPath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[Error] testsuite directory is corrupted");
			return false;
		}

		try {
			boolean allPassed = true;
			String title1;
			String title2;
			String title3;

			// Phase 1
			title1 = "*** Phase 1                               ***";
			title2 = "*** Compiling JavaMOP specifications      ***";
			title3 = "*** into AspectJ monitors (using javamop) ***";
			System.out.println(repeatChar('*', title1.length()));
			System.out.println(title1);
			System.out.println(title2);
			System.out.println(title3);
			System.out.println(repeatChar('*', title1.length()));
			allPassed = traverseTestCaseDir(rootDir, new JavaMOPCompileFunctor());
			System.out.println();

			/*
			 * if (!Main.verbose && allPassed) {
			 * System.out.println("All test cases passed this phase."); }
			 */

			// Phase 2
			title1 = "*** Phase 2                       ***";
			title2 = "*** Compiling Java programs       ***";
			title3 = "*** to be monitored (using javac) ***";
			System.out.println(repeatChar('*', title1.length()));
			System.out.println(title1);
			System.out.println(title2);
			System.out.println(title3);
			System.out.println(repeatChar('*', title1.length()));
			allPassed = traverseTestCaseDir(rootDir, new ProgramCompileFunctor());
			System.out.println();

			/*
			 * if (!Main.verbose && allPassed) {
			 * System.out.println("All test cases passed this phase.");
			 * System.out
			 * .println("(Except ones that have failed in the earlier phases)");
			 * }
			 */

			// Phase 3
			title1 = "*** Phase 3                                    ***";
			title2 = "*** Weaving generated AspectJ monitors         ***";
			title3 = "*** and generated program binaries (using ajc) ***";
			System.out.println(repeatChar('*', title1.length()));
			System.out.println(title1);
			System.out.println(title2);
			System.out.println(title3);
			System.out.println(repeatChar('*', title1.length()));
			allPassed = traverseTestCaseDir(rootDir, new AspectJCompileFunctor());
			System.out.println();

			/*
			 * if (!Main.verbose && allPassed) {
			 * System.out.println("All test cases passed this phase.");
			 * System.out
			 * .println("(Except ones that have failed in the earlier phases)");
			 * }
			 */

			// Phase 4
			title1 = "*** Phase 4                                  ***";
			title2 = "*** Testing the resulting monitored programs ***";
			System.out.println(repeatChar('*', title1.length()));
			System.out.println(title1);
			System.out.println(title2);
			System.out.println(repeatChar('*', title1.length()));
			allPassed = traverseTestCaseDir(rootDir, new TestingFunctor());
			System.out.println();

			/*
			 * if (!Main.verbose && allPassed) {
			 * System.out.println("All test cases passed this phase.");
			 * System.out
			 * .println("(Except ones that have failed in the earlier phases)");
			 * }
			 */

			// Result
			title1 = "*** Testing results ***";
			System.out.println(repeatChar('*', title1.length()));
			System.out.println(title1);
			System.out.println(repeatChar('*', title1.length()));
			int numPassedTestCases = rootDir.numTestCasesOfStatus(true);
			int numFailedTestCases = rootDir.numTestCasesOfStatus(false);
			System.out.println(numPassedTestCases + " test case(s) passed, " + numFailedTestCases + " test case(s) failed.");
			if (numFailedTestCases == 0)
				success = true;
			else
				System.out.println(" - Use -v option for more detailed output of failed test cases.");
			System.out.println();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	public static void main(String[] args) {
		int i = 0;
		String testSuiteDir = null;

		System.out.println("here************************");
		ClassLoader loader = Main.class.getClassLoader();
		String mainClassPath = loader.getResource("javamop/JavaMOPMain.class").toString();
		String rvClassPath = loader.getResource("com/runtimeverification/rvmonitor/java/rvj/Main.class").toString();
		String rvRtClassPath = loader.getResource("com/runtimeverification/rvmonitor/java/rt/observable/IInternalBehaviorObserver.class").toString();
		String logicRepoClassPath = loader.getResource("com/runtimeverification/rvmonitor/" +
				"logicrepository/parser/logicrepositorysyntax/LogicRepositoryType.class").toString();
		
		System.err.println(mainClassPath);
		System.err.println(rvClassPath);
		if (mainClassPath.endsWith(".jar!/javamop/JavaMOPMain.class") && mainClassPath.startsWith("jar:")) {
			isJarFile = true;

			jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length() - "!/javamop/JavaMOPMain.class".length());
			String rvFilePath = rvClassPath.substring("jar:file:".length(), rvClassPath.length() -
					"!/com/runtimeverification/rvmonitor/java/rvj/Main.class".length());
			
			String rvRtFilePath = rvRtClassPath.substring("jar:file:".length(), rvRtClassPath.length() -
					"!/com/runtimeverification/rvmonitor/java/rt/observable/IInternalBehaviorObserver.class".length());
			
			String logicRepoFilePath = logicRepoClassPath.substring("jar:file:".length(), logicRepoClassPath.length() -
					"!/com/runtimeverification/rvmonitor/logicrepository/parser/logicrepositorysyntax/LogicRepositoryType.class".length());
			jarFilePath = jarFilePath + File.pathSeparator + rvFilePath + 
					File.pathSeparator + rvRtFilePath + File.pathSeparator + logicRepoFilePath;
			System.err.println("jar:    " + jarFilePath);
			jarFilePath = polishPath(jarFilePath);
			System.err.println("jar:    " + jarFilePath);
		}

		String base_path = "";
		if (isJarFile) {
			base_path = new File(jarFilePath).getParent();
			base_path = polishPath(base_path);
			try {
				base_path = (new File(base_path)).getCanonicalPath();
			} catch (Exception e) {
			}
		} else {
			base_path = Main.class.getResource(".").getFile();
			base_path = polishPath(base_path) + File.separator + ".." + File.separator;
			try {
				base_path = (new File(base_path)).getCanonicalPath();
			} catch (Exception e) {
			}
		}

		while (i < args.length) {
			if (args[i].compareTo("-h") == 0 || args[i].compareTo("-help") == 0) {
				print_help();
				return;
			} else if (args[i].compareTo("-test") == 0) {
			} else if (args[i].compareTo("-j") == 0 && i + 1 < args.length) {
				++i;
                Main.numThreads = new Integer(args[i]);
			} else if (args[i].compareTo("-v") == 0 || args[i].compareTo("-verbose") == 0) {
				Main.verbose = true;
			} else if (args[i].compareTo("-vf") == 0 || args[i].compareTo("-verboseonfail") == 0) {
				Main.verboseOnFailure = true;
			} else if (args[i].compareTo("-local") == 0) {
				Main.local = true;
			} else if (args[i].compareTo("-remote") == 0) {
				Main.local = false;
			} else if (args[i].compareTo("-a") == 0 && i + 1 < args.length) {
				++i;
				Main.ajcPath = args[i];
			} else if (args[i].compareTo("-m") == 0 && i + 1 < args.length) {
				++i;
				Main.javamopDir = args[i];
			} else {
				if (testSuiteDir == null)
					testSuiteDir = args[i];
				else {
					print_help();
					return;
				}
			}

			++i;
		}

		if (testSuiteDir == null || testSuiteDir.length() == 0) {
			print_help();
			return;
		}

		// Handling default values
		if (Main.ajcPath == null || Main.ajcPath.length() == 0) {
			Main.ajcPath = "ajc";
		}
		if (Main.javamopDir == null || Main.javamopDir.length() == 0) {
			if (Main.isJarFile)
				Main.javamopDir = "";
			else
				Main.javamopDir = base_path + File.separator;
		}

		// getting canonical dir paths
		try {
			if (Main.ajcPath.compareTo("ajc") != 0)
				Main.ajcPath = (new File(Main.ajcPath).getCanonicalPath());
			if (!Main.isJarFile)
				Main.javamopDir = (new File(Main.javamopDir).getCanonicalPath());
		} catch (Exception e) {
		}

		// error cases
		if (Main.ajcPath.compareTo("ajc") != 0 && !new File(Main.ajcPath).exists()) {
			System.out.println(ajcPath);
			System.err.println("[ERROR] the given aspectj compiler does not exist.");
			return;
		}
		if (!Main.isJarFile && !new File(Main.javamopDir).exists()) {
			System.err.println("[ERROR] the given javamop directory does not exist.");
			return;
		}
		try {
			// testSuiteDir = Main.polishPath((new File(".").getCanonicalPath())
			// + File.separator + testSuiteDir);
			testSuiteDir = new File(testSuiteDir).getCanonicalPath();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		if (!new File(testSuiteDir).exists()) {
			System.err.println("[ERROR] the given test suite directory does not exist.");
			return;
		}
		System.out.println();
		System.out.println("*************************");
		System.out.println("*** JavaMOP TestSuite ***");
		System.out.println("*************************");
//		System.out.println();

		System.out.println("This test suite tests JavaMOP by generating monitors ");
		System.out.println("and running testing programs, which have been weaved");
		System.out.println("with the generated monitors, against specified output files.");
		System.out.println();
		System.out.println("For detailed test output, use the \"-v\" (verbose) flag to the testsuite script."); 
		System.out.println();
		System.out.println("See the JavaMOP README file for more information."); 
		System.out.println();
	
	    System.out.println("Are you sure you wish to continue with this test suite [Y/n]?");	
		try {
			char input = (char) System.in.read();
			if(input == 'n' || input == 'N'){
               System.out.println("...Ok, quitting the test suite...");
			   System.exit(0);
			}
			int throwAway;
			 //10 is the ASCII code for return
			if(input != (char)10){
			  while((throwAway = System.in.read()) != 10){}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean success = process(testSuiteDir);

		System.out.print("Press enter key to terminate ..."); 
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!success)
			System.exit(1);
	}
}

class TraverseTask implements Runnable {
  BooleanRef ret;
  TestCaseDir testCaseDir;
  TestCaseFunctor functor;

  TraverseTask(BooleanRef ret, TestCaseDir testCaseDir, TestCaseFunctor functor){
    this.ret = ret;
	this.testCaseDir = testCaseDir;
	this.functor = functor;
  } 

  public void run(){
	boolean b = true;
    try{ 
	  b = Main.traverseTestCaseDir(testCaseDir, functor);
	} catch (Exception e){
      e.printStackTrace();
	}
	synchronized(ret){
      ret.toBool = ret.toBool && b;
	}
  } 
}

class BooleanRef {
  boolean toBool = true;

  BooleanRef(boolean toBool){
    this.toBool = toBool;
  }
}
