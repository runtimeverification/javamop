# JavaMOP Usage

**Note:** This document assumes that you have followed all
the instructions in (../INSTALL.md)[../INSTALL.md] and updated
your CLASSPATH and PATH environment variables according to the
Prerequisites section in that file.
In addition, be cautious whenever you modify or override the
CLASSPATH as it might break the prerequisites of JavaMOP.

Before using JavaMOP, you should specify the properties to be
monitored. The specifications must be stored in files with `.mop`
extension (e.g. `HasNext.mop`).

# Modes of use

JavaMOP currently supports two modes of use:

1. Java Agent

  Java [agents]
  (http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)
  make it possible to instrument programs running on the JVM. This
  approach is easy to use: AspectJ Compiler (ajc) is not needed and all
  the dependencies are contained within the agent. However, using this
  option may incur more runtime overhead, since it weaves the code at runtime.
  JavaMOP does not generate the agent directly; instead, a separate tool 
  called `javamopagent` is responsible for agent generation. Please refer
  to [JavaMOPAgentUsage.md](JavaMOPAgentUsage.md) for detailed instructions
  on how to use JavaMOPAgent.

2. Static Weaving

  Compared to the Java Agent option, Static Weaving has better
  performance, but it requires knowledge of using ajc and resolving
  all the target program's dependencies.

(For a description of all JavaMOP options, please run the following
from a terminal at any time: `javamop -h`)


### Static Weaving Using AspectJ

#### Generating Instrumentation File and RVM Specification

With this mode, you can generate instrumentation (.aj) file(s) and 
RV-Monitor specification(s). The instrumentation file includes the
pointcuts and advice which will be used by ajc to instrument the code.
The RV-Monitor specification (.rvm file) is transformed from .mop 
specification by removing all the aspectJ related annotations, and
when it is fed to [RV-Monitor](https://runtimeverification.com/monitor/), a Java library that performs the actual
monitoring task will be generated. The advice in the instrumentation
file will call the functions provided in the generated monitoring library.
Once JavaMOP is correctly installed, this can be achieved by running the following command:

```javamop [-v] [-d <target directory>] [-merge] <properties>```

The option ```[-v]``` generates the output in verbose mode and 
```[-d <target directory>]``` stores all output files to the 
user-specified directory which must exist prior to issuing the command
above.  ```<properties>``` refers to one or more property (i.e. *.mop)
files, or a directory containing such property files. By default, one
.aj file is generated for each JavaMOP specification. When ```[-merge]```
is set, JavaMOP will generate a combined .aj file for monitoring
multiple properties simultaneously.

#### Generate Monitoring Library using [RV-Monitor](https://runtimeverification.com/monitor/)
Please follow the instructions of [RV-Monitor's online documentation](https://runtimeverification.com/monitor/1.3/docs/) for how to use RV-Monitor.  

#### Weaving the code using ajc

To weave the target program with the generated monitoring library, run
the following command:

```ajc -1.6  -d <target directory> <aj file path> <monitor path> <java file path>```

```-1.6``` indicates the output bytecode version. ```-d <target
directory>``` specifies the directory to which the weaved code will be
stored. Note that you must specify the output directory explicitly so
that ajc can put the binary code in the right place. Without ```-d```,
ajc will output all the bytecode files in the current directory,
failing to keep the necessary package layout. You can simply use ```-d
.``` to output binary code in the current
directory. ```<aj file path>``` and ```<monitor path>``` refer
to the path to the generated instrumentation file and the monitoring 
library (in the form of .java file) respectively; ```<java file path>```
is the path to the target program (i.e the program to be weaved). 
Given this command, ajc will instrument and compile the original 
Java file and store the generated .class file(s) in ```<target directory>```.
If there is no error reported, you can directly run the weaved code in
the ```<target directory>```.

#### Running the Weaved Code
To run the weaved program, simply type:

```java Main```

where `Main` is assumed to be the entry point to the application.
(Don't forget to have the monitoring library on the classpath when
running the weaved code.)

##Troubleshooting

Here we gathered some problems that you might encounter while
using JavaMOP, along with instructions on how to
solve them.

### I get `Code size too big` error when using AspectJ Compiler, what should I do?

In some extreme cases when you need to monitor classes with huge
methods or the intended pointcuts are very dense in some method, you
may encounter some error while using the standard AspectJ compiler to
weave code; as follows:

`/YourBigClass.java [error] problem generating method YourBigClass.bigMethod: Code size too big: 65613.` 

This error is caused by Java's 64KB maximum method size constraint.
If a method of the monitored class is already too big, then, after
inserting the advice at the pointcuts, it may exceed the 64KB
limit. This error has nothing to do with JavaMOP, it is caused by
AspectJ's limitation. 

This kind of problem often happens when you try to weave the bytecode
(.class files) and aspects. If you have access to the source code 
(.java files) of the program that you want to monitor, then the easiest
way that is likely to solve this problem is weaving the source code
(.java files) instead of weaving the compiled code (.class files).
AspectJ compiler will do some optimization to the source code to make it
more space-efficient so that the method size may reduce to the safe zone.

In case you encounter such problem, but either you do not have access to
the source code, or, the above method does not work, then we also provide
a patch for the standard AspectJ source code to solve this problem. 
Please follow the instructions below to install the patch (Please backup
your AspectJ before applying the patch):

**Prerequisites for using the patch:**
 
* AspectJ Compiler source code.

  If you have not checked out the source code of AspectJ, you can go
  [here](http://git.eclipse.org/c/aspectj/org.aspectj.git) to check
  it out.
  
* Ant.

  If you do not have Ant installed, then go
  [here](http://ant.apache.org/) to download and install the latest
  version.

1. Go to your local AspectJ git repository, and checkout the commit
`Fix 443355: interface super references` by executing the command
below (You may need to pull to get the latest updates before you can
perform the operation below):

	``git checkout dddd1236cd21982a07f887ff7fa5d484ebc3b86c``

2. Download the `stdAJC_dddd123.patch` from 
[here](http://fsl.cs.illinois.edu/downloads/stdAJC_dddd123.patch),
and place the patch file in the above AspectJ repository's top level.

3. Apply the patch by executing:

	``git apply --whitespace=nowarn stdAJC_dddd123.patch``
	
4. Build the AspectJ project using ant. You can execute the command
``ant`` at the top level of the AspectJ repository.

5. Deploy the AspectJ libraries. If the previous step is successful,
there will be a new folder called `aj-build` appearing at the top
level of the repository. Add all the jar files in
`<path-to-AspectJ-repository>/aj-build/dist/tools/lib` to your
CLASSPATH.

After generating the new AspectJ libraries and deploying them, your
AspectJ compiler should now be able to handle the classes with huge
methods.

### I get error when I use `Xbootclasspath`, what should I do ?

Instrumenting with Xbootclasspath can lead to errors if the right
jar files are not passed to the Java command after weaving. 
For example, one may see the following error message when 
running something with Xbootclasspath:

	Error occurred during initialization of VM
	java.lang.NoSuchMethodError: sun.misc.JavaLangAccess.registerShutdownHook(ILjava/lang/Runnable;)V
    		at java.io.Console.<clinit>(Console.java:493)
    		at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    		at sun.misc.SharedSecrets.getJavaIOAccess(SharedSecrets.java:93)
    		at java.lang.System.initializeSystemClass(System.java:1089)
    		
The minimum necessary (for JDK 1.6.0.24 on a Linux OS) is

`-Xbootclasspath/p:directoryWithInstrumentedJRE:/usr/lib/jvm/java-6-sun-1.6.0.24/jre/lib/rt.jar`

### I get `Could not find or load main class` error when I use `java`, what should I do ?

This document assumes that you have "." in your CLASSPATH (which is almost always the case). If you do not have it, add it to your CLASSPATH and run that command agian. 

### I get errors when I use `java` or `ajc` with `-cp` (or `-classpath`) option, what should I do ?

JavaMOP (and the files it generates) depend on certain libraries to be on your CLASSPATH. `-cp` and `-classpath` options override your CLASSPATH. So make sure to include the old CLASSPATH in your new one.

### I did not find a solution to my problem here, what should I do ?

Open a new issue on  [JavaMOP's issues page]
(https://github.com/runtimeverification/javamop/issues).

