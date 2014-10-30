#JavaMOP INSTALL

Here are instructions for installing JavaMOP from its binary release
zip archive. Users who checked out the sources should follow the
instructions in src/README.md to build JavaMOP.

##Prerequisites

To use JavaMOP, you need JDK, AJC and RV-Monitor.

1. [JDK]
(http://www.oracle.com/technetwork/java/javase/downloads/index.html)
v.7.0.0 or higher (JRE suffices if you don't generate agents with
JavaMOP)

 * Check Java is installed properly: run `java -version` from a
  terminal.

2. [AJC] (http://www.eclipse.org/aspectj/downloads.php) v.1.8.1 or
higher
 
 * Check AspectJ is installed properly: run `ajc -version` from a
   terminal.

 * Add `<AspectJ_HOME>/lib/aspectjrt.jar`,
   `<AspectJ_HOME>/lib/aspectjweaver.jar` and
   `<AspectJ_HOME>/lib/aspectjtools.jar` to your CLASSPATH.

3. [RV-Monitor] (https://www.runtimeverification.com/monitor) v.1.3 or
higher

 * Check RV-Monitor is installed properly: run `rv-monitor` from a
   terminal.

 * Add `<RV-Monitor_HOME>/lib/rvmonitor.jar` and
 `<RV-Monitor_HOME>/lib/rvmonitorrt.jar` to your CLASSPATH.

##Install

1. Download JavaMOP's binary from [its website]
(http://fsl.cs.illinois.edu/index.php/JavaMOP4).

2. Unzip the file to your preferred location.

##Use

`<JavaMOP_HOME>/bin/javamop` is the target binary. For convenient
usage, update your `PATH` variable with `<JavaMOP_HOME>/bin`.

See README.md for info on how to use JavaMOP.

#Troubleshooting

To get help or report problems go to [JavaMOP's issues]
(https://github.com/runtimeverification/javamop/issues).
