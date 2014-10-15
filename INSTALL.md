#JavaMOP INSTALL

This file contains instructions for using JavaMOP out of the box.

Users who checked out the sources should follow the instructions in
src/README.md to build JavaMOP.

##Prerequisites

To use JavaMOP you need JRE, AJC and RV-Monitor.

1. JRE

 Java Runtime Environment version 7 (http://java.com/en/download/index.jsp)

* To make sure java is installed properly, call `java -version` in a
  terminal.

2. AJC

 AspectJ Compiler version 1.8.1 or higher
 (http://www.eclipse.org/aspectj/downloads.php)

 * Make sure that `<Path-to-AspectJ_HOME>/bin` is in your `PATH` by
   calling `ajc` in a terminal.

 * Also ensure that `<Path-to-AspectJ_HOME>/lib/aspectjrt.jar`,
   `<Path-to-AspectJ_HOME>/lib/aspectjweaver.jar` and
   `<Path-to-AspectJ_HOME>/lib/aspectjtools.jar` are in your
   CLASSPATH.

3. RV-Monitor

 The RV-Monitor libraries. (https://www.runtimeverification.com/monitor)

 1. Download RV-Monitor installer from above website and install it to
 your desired directory.

 2. Add `<path-to-rvmonitorrt.jar>` and `<path-to-rvmonitor.jar>` to
 your CLASSPATH. In the current version of RV-Monitor, those jars are
 under lib/ inside the directory of RV-Monitor, and are named as
 rv-monitor-0.2-SNAPSHOT.jar and rvmonitorrt-0.2-SNAPSHOT.jar
 respectively.


##Install

1. Download JavaMOP's binary from [its website]
(http://fsl.cs.illinois.edu/index.php/JavaMOP4).

2. Unzip the file in your preferred location.

##Use

`<path-to-javamop>/bin/javamop` is the target binary. For convenient
usage, update your `PATH` environment variable with
`<path-to-javamop>/bin/`.

For information regarding usage of JavaMOP, please refer to the
README.md file.

#Troubleshooting

If you experience any problems installing JavaMOP, please open a new
issue on
[Github](https://github.com/runtimeverification/javamop/issues)
