# JavaMOP README

## Overview

Monitoring-Oriented Programming (MOP), is a software development and
analysis framework which aims to reduce the gap between formal
specification and implementation by allowing them together to form a
system. In MOP, runtime monitoring is supported and encouraged as a
fundamental principle for building reliable software: monitors are
automatically synthesized from specified properties and integrated
with the original system to check its dynamic behaviors during
execution. When a specification is violated or validated at runtime,
user-defined actions will be triggered, which can be any code: from
information logging to runtime recovery.  MOP may be understood from
at least three perspectives: (a) as a discipline allowing one to
improve safety, reliability and dependability of a system by
monitoring its requirements against its implementation at runtime; (b)
as an extension of programming languages with logics (one can add
logical statements anywhere in the program, referring to past or
future states); and (c) as a lightweight formal method.

JavaMOP is an instance of MOP for Java.

## Install

Refer to the INSTALL.md file for installing JavaMOP from the [released
zip archive](http://fsl.cs.illinois.edu/index.php/JavaMOP4).  To build
JavaMOP from sources, please download the [source
code](https://github.com/runtimeverification/javamop), and refer to
the src/README file.

**Note:** The rest of this document assumes that you have followed all
the instructions in INSTALL.md and updated your CLASSPATH and PATH
environment variables according to the Prerequisites section in that
file.  In addition, please make sure the dependencies mentioned in
INSTALL.md remain on the CLASSPATH whenever you modify or overwrite
the CLASSPATH.

## Usage

Before using JavaMOP, you should specify the properties you want to be
monitored. The specifications must be stored in files with `.mop`
extension (e.g. `HasNext.mop`).

JavaMOP currently supports two modes of use:

1. Java Agent

  Java [agents]
  (http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)
  make it possible to instrument programs running on the JVM. This
  option is the easiest one to use. Moreover, the user does not need
  AspectJ compiler (ajc), or to know how to resolve dependencies in
  the target program. However, using this option may incur more
  runtime overhead, since it weaves the code at runtime.

2. Static Weaving

  Compared to the Java Agent option, Static Weaving has better
  performance, but it requires users to know how to use ajc and to
  resolve all the target program's dependencies by themselves.

(For a description of all JavaMOP options, please run the following
from a terminal at any time: `javamop -h`)

### Building and using a Java Agent

#### Building a Java Agent

With this mode, users may build Java agents for runtime
instrumentation of their applications. Once JavaMOP is correctly
installed (see the INSTALL.md file in this directory), this may be
achieved by running the following command:

```javamop -agent [-n agentName] [-v] [-d <target directory>] <properties>```

The optional ```[-n agentName]``` specifies "agentName" as the name of
the agent generated, ```[-v]``` generates the agent in verbose mode
and ```[-d <target directory>]``` stores all intermediate files from
agent generation in a user specified directory which must exist prior
to issuing the command above. ```<properties>``` refers to one or more
property (i.e. *.mop) files, or a directory containing such property
files.

If the user specifies the ```[-n agentName]``` option, the previous command
will create ```<agentName>.jar``` in the same directory as that from which
the command is run. If a ```[-n agentName]``` is not specified and there is
just one specification, then an agent with the same name as the
specification will be generated. Finally, if ```[-n agentName]``` is not
specified and there are multiple specification files, then an agent
called "JavaMOPAgent_1.jar" will be generated.

Regarding the properties for building an agent, users can either
choose to write their own properties or use the properties that we
have already formalized from Java API. If users decide to write their
own properties, they need to declare those properties to be in
`package mop;` This is because JavaMOP is using some internal helper
classes inside that package in the process. (It is not necessary for
the property file(s) to be physically placed inside a directory called
"mop"; all that is required is to make the statement, "package mop;"
to be the first line in the property file). Please refer to [JavaMOP
syntax] (http://fsl.cs.illinois.edu/index.php/JavaMOP4_Syntax) page
for more information on writing your own property files.

We have formalized some properties from the Java API. If you are
interested to build a java agent to monitor all these properties,
please run the following command:

```javamop -agent [-n agentName] [-v] [-d <target directory>]
-usedb```

The ```-usedb``` option fetches our properties (formalized from the
Java API) from this URL:

`https://github.com/runtimeverification/property-db/tree/master/annotated-java-api/java`

Using ```-usedb``` requires an internet connection and will ensure
that you get the latest version of these properties. The first time
the above command is run, it makes a copy of the properties
directory. That way, subsequent runs from the same directory do not
require an internet connection, unless the properties directory is
deleted.

As a separate step, we encourage the reader to manually download the
properties from the URL given above, place them in folder and generate
an agent with the command:

```javamop -agent [-n agentName] [-v] [-d <target directory>]
<properties>```,

where ```<properties>``` is replaced with the directory where the
reader has stored the properties. It may also be educational to open
one or two of the ```.mop``` files to learn how properties are
written.

#### Using a Java Agent

Assuming that an agent called "JavaMOPAgent.jar" has been built using
any of the commands from the previous section, such an agent may be
run as follows:

1. For projects with a well-defined entry point such as a Main class,
   first compile the source code and then run the following command:

   ```java -javaagent:JavaMOPAgent.jar Main```

   In other words, you will need to run the same command as you would
   normally use for running your java application, but with the
   addition of the ```-javaagent:JavaMOPAgent.jar```, as shown above.


2. For Maven-based projects which have tests, users can simply run
   ```mvn test```, after modifying the individual project's
   ```pom.xml``` to have an element like the following:

  ```xml
    <build>
    	<plugins>
    		...
        	<plugin>
	  		<groupId>org.apache.maven.plugins</groupId>
	  		<artifactId>maven-surefire-plugin</artifactId>
	  		<version>${surefire-version}</version>
	  		<configuration>
        			<argLine>-javaagent:JavaMOPAgent.jar</argLine>
	  		</configuration>
        	</plugin>
		...
      	</plugins>
     </build>
   ```

   Replace ```${surefire-version}``` with the exact surefire plugin
   version used by the project (e.g., 2.16).

   Adding the javaagent to the `pom.xml` file is the only change
   needed to an existing project and tests can still be run with
   ```mvn test```, as usual.

3. For Ant-based projects which have tests, users can also modify
   ```build.xml``` to run all tests with JavaMOP agent. All that is
   needed is to add one line under the ```junit``` task as follows:

  ```xml
    <target name=...>
    	<junit ...>
    		...
        	<jvmarg value="-javaagent:JavaMOPAgent.jar"/>
		...
      	</plugins>
     </target>
   ```

   After that, users can run their tests as usual by using ```ant
   ${test_target_name}```.

4. Java agent is easily integrated into IDEs like IntelliJ, Eclipse,
etc.
   
   For IntelliJ:
   click the "Run" tab 
             -> select the "Edit Configurations" tab 
             -> select the application you are running 
             -> select "configuration" tab 
             -> enter "javaagent:JavaMOPAgent.jar" in the "VM options" textbox. 
   
   For Eclipse:
   click "Run" tab 
         -> select "Run configurations" tab 
         -> select the application you are running 
         -> select "Arguments" tab 
         -> enter "javaagent:JavaMOPAgent.jar" into "VM options" textbox.
   
   By doing this, you can run or debug programs with the java agent
   from within your IDE.
   

#### Agent generation examples

To build a java agent and run it using one of the examples that ship
with JavaMOP, run the following commands from the same directory as
this file:

```
cd examples/agent/many
javamop -agent -n JavaMOPAgent rvm/
javac SafeMapIterator_1.java
java -javaagent:JavaMOPAgent.jar SafeMapIterator_1
```

Note that running ```javamop -agent -n JavaMOPAgent rvm/``` as above
will print the specification used in building the agent, and give a
"JavaMOPAgent.jar is generated." message at the end, if everything
goes well.

Also, running ```java -javaagent:JavaMOPAgent.jar SafeMapIterator_1```
as above will run the specified java program and, if everything works,
will print out the following messages:

```
unsafe iterator usage!
unsafe iterator usage!
! hasNext() has not been called before calling next() for an iterator
java found the problem too
```

### Static Weaving Using AspectJ

#### Generating Instrumentation File and Java Library

With this mode, users can generate an instrumentation (.aj) file and a
java library (.java) file to be weaved into the target program. The
instrumentation file includes the pointcuts and advice which will be
used by the AspectJ compiler (ajc) to instrument the code. The advice
in the instrumentation file will call the functions provided in the
java library. For simplicity, JavaMOP appends the java library file to
the instrumentation file and generates a single .aj file in the
end. Once JavaMOP is correctly installed (see the INSTALL.md file in
this directory), this can be achieved by running the following
command:

```javamop [-v] [-d <target directory>] [-merge] <properties>```

The option ```[-v]``` generates the file and the library in verbose
mode and ```[-d <target directory>]``` stores all output files to the
user-specified directory which must exist prior to issuing the command
above.  ```<properties>``` refers to one or more property (i.e. *.mop)
files, or a directory containing such property files. By default, one
.aj file is generated for each JavaMOP specification. When
```[-merge]``` is set, JavaMOP will generate a combined .aj file for
monitoring multiple properties simultaneously.

#### Weaving the code using AspectJ Compiler (ajc)

Before weaving the code, make sure that you have already installed ajc
and RV-Monitor. Please refer to INSTALL.md for prerequisites of using
JavaMOP (installing JRE, AJC and RV-Monitor).

To weave the target program with the generated monitoring library, run
the following command:

```ajc -1.6  -d <target directory> <path-to-aj-file> <path-to-java-file>```

```-1.6``` indicates the output bytecode version. ```-d <target
directory>``` specifies the directory to which the weaved code will be
stored. Note that you must specify the output directory explicitly so
that ajc can put the binary code in the right place. Without ```-d```,
ajc will output all the bytecode files in the current directory,
failing to keep the necessary package layout. You can simply use ```-d
.``` to output binary code in the current
directory. ```<path-to-aj-file>``` and ```<path-to-java-file>``` refer
to the path to the generated instrumentation file and the path to the
target program (i.e the program to be weaved) respectively. Given this
command, ajc will instrument and compile the original java file and
store the generated .class file(s) in ```<target directory>```. If
there is no error reported, you can directly run the weaved code in
the ```<target directory>```.

(For more information on ajc options, type ```ajc -help``` for help)

**Note:** As mentioned before, certain files (see Prerequisites in
INSTALL.md) must be present on the java class path.  If you have
additional dependencies and you want to add them with `-cp` (or
`-classpath`) option, please make sure that those files remain on the
classpath.

#### Running the Weaved Code
To run the weaved program, simply type:

```java Main```

where `Main` is the entry point to the application.

**Note:** Again, make sure that pre-requisites are in the java class
path specially when you use `-cp` (or `-classpath`) option.

### Troubleshooting

In some extreme cases when you need to monitor classes with huge
methods or the intended pointcuts are very dense in some method, you
may encounter some error while using the standard AspectJ compiler to
weave code; as follows:

`/YourBigClass.java [error] problem generating method YourBigClass.bigMethod: Code size too big: 65613.` 

This error is caused by Java's 64KB maximum method size constraint.
If a method of the monitored class is already too big, then, after
inserting the advice at the pointcuts, it may exceed the 64KB
limit.

If you have access to the source code (.java files) of the program
that you want to monitor, then the easiest way to solve this problem
is weaving the source code (.java files) instead of weaving the
compiled code (.class). The AspectJ compiler optimizes the source code
to make it more space-efficient so that it will not violate the method
size constraint during weaving. This error has nothing to do with
JavaMOP, it is caused by AspectJ's limitation. This kind of problem
often happens when you try to weave the bytecode (.class files) and
aspects. If you have access to the source code (.java files) of the
program that you want to monitor, then the easiest way that is likely
to solve this problem is weaving the source code (.java files) instead
of weaving the compiled code (.class files). AspectJ compiler will do
some optimization to the source code to make it more space-efficient
so that the method size may reduce to the safe zone.

In case you encounter such problem, but either you do not have access to
the source code, or, the above method does not work, then we also provide
a patch for the standard AspectJ source code to solve this problem. 
Please follow the instructions below to install the patch (Please backup
your AspectJ before applying the patch):

**Prerequisites for using the patch:**
 
* AspectJ source code.

  If you have not checked out the source code of AspectJ, you can go
  to [here](http://git.eclipse.org/c/aspectj/org.aspectj.git) to check
  it out.
  
* Ant.

  If you do not have Ant installed, then go to
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

####Complete list for troubleshooting 

If your problem is not listed in the above section, you can go to
[here](http://fsl.cs.illinois.edu/index.php/JavaMOP4_Troubleshooting)
for a complete list of common issues and their solutions.

## Contact Information

We welcome your interest in JavaMOP. Your feedback, comments and bug
reports are highly appreciated. Please feel free to contact us by
opening new issues on
[Github](https://github.com/runtimeverification/javamop/issues)
