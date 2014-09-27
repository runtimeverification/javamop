# JavaMOP README
## Overview

Monitoring-Oriented Programming, abbreviated MOP, is a software
development and analysis framework aiming at reducing the gap between
formal specification and implementation by allowing them together to
form a system. In MOP, runtime monitoring is supported and encouraged
as a fundamental principle for building reliable software: monitors
are automatically synthesized from specified properties and integrated
into the original system to check its dynamic behaviors during
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

## Usage

JavaMOP currently supports two modes of use:

(For a description of all JavaMOP options, please type the following at
any time: ```javamop -h```)

### Building and using a Java Agent

#### Building a Java Agent

In this mode, the user may build a Java agent for runtime
instrumentation of their applications. Once JavaMOP is correctly
installed (see the INSTALL.md file in this directory), this may be
achieved by running the following command:

```javamop -agent [-n agentName] [-v] [-d <target directory>] <specification file or dir>```

If the user specifies the [-n agentName], the above command will
create <agentName>.jar in the same directory as that from which the
command is run. If a [-n agentName] is not specified and there is just
one specification, then an agent with the same name as the
specification will be generated. Finally, if [-n agentName] is not
specified and there are multiple specification files, then an agent
called "MultiSpec_1.jar" is generated.

We have formalized some properties from the Java API. If you are
interested to build a java agent to monitor all these properties,
please run the following command:

```javamop -agent [-n agentName] [-v] [-d <target directory>] -usedb```

The optional ```[-n agentName]``` specifies "agentName" as the name of
the agent generated, ```[-v]``` generates the agent in verbose mode
and ```[-d <target directory>]``` will store all intermediate files
from agent generation in a user specified directory which must exist
before prior to issuing the command above.

The ```-usedb``` option fetches the latest [set of
properties](https://github.com/runtimeverification/property-db/tree/master/annotated-java-api/java)
which we formalized from the Java API and requires an internet
connection. That way you will get the latest version of these
properties at any point in time. The first time the above command is
run, it makes a copy of the properties directory. That way, subsequent
runs from the same directory does not require an online connection,
unless the properties directory is deleted.

#### Using A Java Agent

Assuming that an agent called "agent.jar" has been built following any
of the commands from the previous section, such an agent may be run as
follows:

1. For projects with a well-defined entry point such as a Main class,
   first compile the source code and run the following command:
  
   ```java -javaagent:agent.jar -cp .:[other dependencies] Main```

   In order words, you will need to run same command as you would
   normally use for running your java application, but with the addition
   of the ```-javaagent:agent.jar```, as shown above.


2. For Maven-based projects which have tests, can simply run ```mvn
   test```, after modifying the individual projects ```pom.xml``` to have
   an element like the following:

  ```xml
    <build>
    	<plugins>
    		...
        	<plugin>
	  		<groupId>org.apache.maven.plugins</groupId>
	  		<artifactId>maven-surefire-plugin</artifactId>
	  		<version>${surefire-version}</version>
	  		<configuration>
        			<argLine>-javaagent:agent.jar</argLine>
	  		</configuration>
        	</plugin>
		...       
      	</plugins>
     </build>
   ```

   Replace ```${surefire-version}``` with the exact surefire plugin
   version used by the project (e.g., 2.16). 

   Adding the javaagent is the only change needed to an existing
   project and tests can still be run with ```mvn test```, as usual.

#### Putting it all together

To build a java agent and run it using some of the examples that ship
with JavaMOP, run the following commands from the same directory as
this file:

```
cd examples/agent/many
javamop -agent -n agent rvm 
javac SafeMapIterator_1.java 
java -javaagent:agent.jar -cp . SafeMapIterator_1
```

Note that running ```javamop -agent -n agent rvm``` as above will
print the specifications used in building the agent, and give a
"agent.jar is generated." message at the end, if everything goes well.

Also, running ```java -javaagent:agent.jar -cp . SafeMapIterator_1```
as above will run the specified java program and, if everything works,
will print out the following violation messages:

```
unsafe iterator usage!
unsafe iterator usage!
! hasNext() has not been called before calling next() for an iterator
java found the problem too
```

### Static Weaving Using AspectJ

(We will update this Section soon)

## Contact Information

We welcome your interest in JavaMOP. Your feedback, comments and bug
reports are highly appreciated. Please feel free to contact us by
opening new issues on
[Github](https://github.com/runtimeverification/javamop/issues)
