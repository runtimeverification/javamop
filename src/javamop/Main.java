/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package javamop;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;

import javamop.logicclient.LogicRepositoryConnector;
import javamop.util.Tool;

class JavaFileFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return name.endsWith(".java");
	}
}

class MOPFileFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return name.endsWith(".mop");
	}
}

public class Main {
	static String output_path = null;
	public static boolean debug = false;
	public static boolean noopt1 = false;
	public static boolean toJavaLib = false;
	public static boolean statistics = false;
	public static String aspectname = null;
	public static boolean isJarFile = false;
	public static String jarFilePath = null;
	
	/**
	 * Process a java file including mop annotations to generate an aspectj
	 * file. The path argument should be an existing java file name. The
	 * location argument should contain the original file name, But it may have
	 * a different directory.
	 *
	 * @param path
	 *            an absolute path of a specification file
	 * @param location
	 *            an absolute path for result file
	 */
	public static void processJavaFile(String path, String location) throws Exception {
		String content = Tool.convertFileToString(path);

		MOPNameSpace.init();
		AnnotationProcessor processor = new AnnotationProcessor(Main.aspectname == null ? Tool.getFileName(path) : Main.aspectname);

		String aspect = processor.process(content);
		writeAspectFile(aspect, location);
	}

	/**
	 * Process a specification file to generate an aspectj file. The path
	 * argument should be an existing specification file name. The location
	 * argument should contain the original file name, But it may have a
	 * different directory.
	 *
	 * @param path
	 *            an absolute path of a specification file
	 * @param location
	 *            an absolute path for result file
	 */
	public static void processSpecFile(String path, String location) throws Exception {
		String content = Tool.convertFileToString(path);

		MOPNameSpace.init();
		SpecificationProcessor processor = new SpecificationProcessor(Main.aspectname == null ? Tool.getFileName(path) : Main.aspectname);

		String output = processor.process(content);

		if (toJavaLib) {
			writeJavaLibFile(output, location);
		} else {
			writeAspectFile(output, location);
                }
	}

	protected static void writeJavaFile(String javaContent, String location) throws Exception {
		if ((javaContent == null) || (javaContent.length() == 0))
			throw new MOPException("Nothing to write as a java file");
		if (!Tool.isJavaFile(location))
			throw new MOPException(location + "should be a Java file!");

		FileWriter f = new FileWriter(location);
		f.write(javaContent);
		f.close();
	}

	protected static void writeAspectFile(String aspectContent, String location) throws Exception {
		if (aspectContent == null || aspectContent.length() == 0)
			return;

		int i = location.lastIndexOf(File.separator);
		FileWriter f = new FileWriter(location.substring(0, i + 1) + Tool.getFileName(location) + "MonitorAspect.aj");
		f.write(aspectContent);
		f.close();
		System.out.println(" " + Tool.getFileName(location) + "MonitorAspect.aj is generated");
	}

	protected static void writeJavaLibFile(String javaLibContent, String location) throws Exception {
		if (javaLibContent == null || javaLibContent.length() == 0)
			return;

		int i = location.lastIndexOf(File.separator);
		FileWriter f = new FileWriter(location.substring(0, i + 1) + Tool.getFileName(location) + "JavaLibMonitor.java");
		f.write(javaLibContent);
		f.close();
		System.out.println(" " + Tool.getFileName(location) + "JavaLibMonitor.java is generated");
	}

	// PM
	protected static void writePluginOutputFile(String pluginOutput, String location) throws Exception {
		int i = location.lastIndexOf(File.separator);
		FileWriter f = new FileWriter(location.substring(0, i + 1) + Tool.getFileName(location) + "PluginOutput.txt");
		f.write(pluginOutput);
		f.close();
		System.out.println(" " + Tool.getFileName(location) + "PluginOutput.txt is generated");
	}

	public static void process(String arg) {
		process(arg.split(";"), "");
	}

	public static String polishPath(String path) {
		if (path.indexOf("%20") > 0)
			path = path.replaceAll("%20", " ");

		return path;
	}

	public static void process(String[] files, String path) {
		// iterate over every supplied file
		for (String file : files) {
			String fPath = path.length() == 0 ? file : path + File.separator + file;

			File f = new File(fPath);

			// if the file does not exist, alert the user
			if (!f.exists()) {
				System.err.println("[Error] Target file, " + file + ", doesn't exsit!");
				System.err.println("");
				// if it is a directory, recursively process all the files in it
			} else if (f.isDirectory()) {
				process(f.list(new JavaFileFilter()), f.getAbsolutePath());
				process(f.list(new MOPFileFilter()), f.getAbsolutePath());
			} else {
				try {
					String location = output_path == null ? f.getAbsolutePath() : output_path + File.separator + f.getName();
					System.out.println("-Processing " + file);
					if (Tool.isSpecFile(file)) {
						processSpecFile(f.getAbsolutePath(), location);
					} else if (Tool.isJavaFile(file)) {
						processJavaFile(f.getAbsolutePath(), location);
					} else
						throw new MOPException("Unrecognized file type! The JavaMOP specification file should have .mop as the extension.");
				} catch (Exception e) {
					// any exceptions should be printed to the user on stderr
					System.err.println(" [Error]" + e.getMessage());
					if (Main.debug)
						e.printStackTrace();
				}
				System.out.println("");
			}
		}
	}

	// PM
	public static void print_help() {
		System.out.println("Usage: java [-cp javmaop_classpath] javamop.Main [-options] files");
		System.out.println("");
		System.out.println("where options include:");
		System.out.println(" Options enabled by default are prefixed with \'+\'");
		System.out.println("    -h -help\t\t\t  print this help message");
		System.out.println("    -v | -verbose\t\t  enable verbose output");
		System.out.println("    -debug\t\t\t  enable verbose error message");
		System.out.println();

		System.out.println("    -local\t\t\t+ use local logic engine");
		System.out.println("    -remote\t\t\t  use default remote logic engine");
		System.out.println("\t\t\t\t  " + Configuration.getServerAddr());
		System.out.println("\t\t\t\t  (You can change the default address");
		System.out.println("\t\t\t\t   in javamop/config/remote_server_addr.properties)");
		System.out.println("    -remote:<server address>\t  use remote logic engine");
		System.out.println();

		System.out.println("    -d <output path>\t\t  select directory to store output files");
		System.out.println("    -n | -aspectname <aspect name>\t  use the given aspect name instead of source code name");
		System.out.println();

		System.out.println("    -s | -statistics\t\t  generate monitor with statistics");
		System.out.println("    -noopt1\t\t\t  don't use the enable set optimization");
		System.out.println("    -javalib\t\t\t  generate a java library rather than an AspectJ file");
		System.out.println();

		System.out.println("    -aspect:\"<command line>\"\t  compile the result right after it is generated");
		System.out.println();
	}

	public static void main(String[] args) {
		ClassLoader loader = Main.class.getClassLoader();
		String mainClassPath = loader.getResource("javamop/Main.class").toString();
		if (mainClassPath.endsWith(".jar!/javamop/Main.class") && mainClassPath.startsWith("jar:")) {
			isJarFile = true;

			jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length() - "!/javamop/Main.class".length());
			jarFilePath = polishPath(jarFilePath);
		}

		int i = 0;
		String files = "";

		while (i < args.length) {
			if (args[i].compareTo("-h") == 0 || args[i].compareTo("-help") == 0) {
				print_help();
				return;
			}

			if (args[i].compareTo("-d") == 0) {
				i++;
				output_path = args[i];
			} else if (args[i].compareTo("-local") == 0) {
				LogicRepositoryConnector.serverName = "local";
			} else if (args[i].compareTo("-remote") == 0) {
				LogicRepositoryConnector.serverName = "default";
			} else if (args[i].startsWith("-remote:")) {
				LogicRepositoryConnector.serverName = args[i].substring(8);
			} else if (args[i].compareTo("-v") == 0 || args[i].compareTo("-verbose") == 0) {
				LogicRepositoryConnector.verbose = true;
				MOPProcessor.verbose = true;
			} else if (args[i].compareTo("-javalib") == 0) {
				toJavaLib = true;
			} else if (args[i].compareTo("-debug") == 0) {
				Main.debug = true;
			} else if (args[i].compareTo("-noopt1") == 0) {
				Main.noopt1 = true;
			} else if (args[i].compareTo("-s") == 0 || args[i].compareTo("-statistics") == 0) {
				Main.statistics = true;
			} else if (args[i].compareTo("-n") == 0 || args[i].compareTo("-aspectname") == 0) {
				i++;
				Main.aspectname = args[i];
			} else {
				if (files.length() != 0)
					files += ";";
				files += args[i];
			}
			++i;
		}

		if (files.length() == 0) {
			print_help();
			return;
		}

		process(files);
	}
}
