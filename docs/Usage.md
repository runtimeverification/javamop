# JavaMOP Usage

**Note:** This document assumes that you have followed all
the instructions in INSTALL.md and updated your CLASSPATH and PATH
environment variables according to the Prerequisites section in that
file. In addition, we also assume that "." is on your CLASSPATH. Last
but not least, be cautious whenever you modify or override the
CLASSPATH as it might break the prerequisites of JavaMOP.

Before using JavaMOP, you should specify the properties to be
monitored. The specifications must be stored in files with `.mop`
extension (e.g. `HasNext.mop`).

# Mods of use

JavaMOP currently supports two modes of use:

1. Java Agent

  Java [agents]
  (http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)
  make it possible to instrument programs running on the JVM. This
  option is the easiest one to use: AspectJ Compiler (ajc) is not
  needed and all the dependencies are contained within the
  agent. However, using this option may incur more runtime overhead,
  since it weaves the code at runtime.

2. Static Weaving

  Compared to the Java Agent option, Static Weaving has better
  performance, but it requires knowledge of using ajc and resolving
  all the target program's dependencies.

(For a description of all JavaMOP options, please run the following
from a terminal at any time: `javamop -h`)

### Building and using a Java Agent

#### Building a Java Agent

With this mode, you may build Java agents for runtime instrumentation
of your applications. Once JavaMOP is correctly installed, this can
be achieved by running the following command:

```javamop -agent [-n agentName] [-v] [-d <target directory>] <properties>```

The optional ```[-n agentName]``` specifies "agentName" as the name of
the agent generated, ```[-v]``` generates the agent in verbose mode
and ```[-d <target directory>]``` stores all intermediate files from
agent generation in a user specified directory which must exist prior
to issuing the command above. ```<properties>``` refers to one or more
property (i.e. *.mop) files, or a directory containing such property
files.

If you specify the ```[-n agentName]``` option, the previous command
will create ```<agentName>.jar``` in the same directory as that from which
the command is run. If a ```[-n agentName]``` is not specified and there is
just one specification, then an agent with the same name as the
specification will be generated. Finally, if ```[-n agentName]``` is not
specified and there are multiple specification files, then an agent
called "JavaMOPAgent_1.jar" will be generated.

Regarding the properties for building an agent, you can either choose
to write your own properties or use the properties that we have
already formalized from Java API. If you decide to write your own
properties, you need to declare those properties to be in `package
mop;` This is because JavaMOP is using some internal helper classes
inside that package in the process. (It is not necessary for the
property file(s) to be physically placed inside a directory called
"mop"; all that is required is to make the statement, "package mop;"
to be the first line in the property file). Please refer to [JavaMOP
syntax] (http://fsl.cs.illinois.edu/index.php/JavaMOP4_Syntax) page
for more information on writing your own property files.

We have formalized a large number of properties from the Java API. If
you are interested in building a java agent to monitor all these
properties, please run the following command:

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

As a separate step, we also encourage the reader to manually download
the properties from the URL given above, place them in a separate
folder and generate an agent with the command:

```javamop -agent [-n agentName] [-v] [-d <target directory>]
<properties>```,

where ```<properties>``` is replaced with the directory where the
reader has stored the properties. It may also be educational to open a
few of the ```.mop``` files to learn how to write JavaMOP properties.

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


2. For Maven-based projects with tests, you can modify ```pom.xml```
to use the agent when running tests by adding following lines:

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

   After that, you can run tests with the agent as usual by using
   ```mvn test```.

3. For Ant-based projects which have tests, you can modify
   ```build.xml``` to use the agent when running tests by adding one
   line under the ```junit``` task:

  ```xml
    <target name=...>
    	<junit ...>
    		...
        	<jvmarg value="-javaagent:JavaMOPAgent.jar"/>
		...
      	</plugins>
     </target>
   ```

   After that, you can run tests with the agent as usual by using
   ```ant ${test_target_name}```.

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
   
   By doing this, you will be able to run or debug programs with the
   agent within your IDE.
   

#### Agent generation examples

To build an agent and run it using one of the examples that shipped
with JavaMOP, run following commands from the same directory as in
this example:


```
cd examples/agent/many
javamop -agent -n JavaMOPAgent rvm/
javac SafeMapIterator_1.java
java -javaagent:JavaMOPAgent.jar SafeMapIterator_1
```

Note that running ```javamop -agent -n JavaMOPAgent rvm/``` as above
will print the specification used for building the agent, and print
out message "JavaMOPAgent.jar is generated." at the end, which
indicates everything goes well.

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

With this mode, you can generate an instrumentation (.aj) file and a
java library (.java) file to be weaved into the target program. The
instrumentation file includes the pointcuts and advice which will be
used by ajc to instrument the code. The advice in the instrumentation
file will call the functions provided in the Java library. For
simplicity, JavaMOP appends the java library file to the
instrumentation file and generates a single .aj file in the end. Once
JavaMOP is correctly installed, this can be achieved by running the
following command:

```javamop [-v] [-d <target directory>] [-merge] <properties>```

The option ```[-v]``` generates the file and the library in verbose
mode and ```[-d <target directory>]``` stores all output files to the
user-specified directory which must exist prior to issuing the command
above.  ```<properties>``` refers to one or more property (i.e. *.mop)
files, or a directory containing such property files. By default, one
.aj file is generated for each JavaMOP specification. When
```[-merge]``` is set, JavaMOP will generate a combined .aj file for
monitoring multiple properties simultaneously.

#### Weaving the code using ajc

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

**Note:** If you have additional dependencies, you may add them with
`-cp` (or `-classpath`) option. Please be careful when using this
option because it will override your CLASSPATH. This suggestion
applies to both ```ajc``` and ```java```.

#### Running the Weaved Code
To run the weaved program, simply type:

```java Main```

where `Main` is assumed to be the entry point to the application.
