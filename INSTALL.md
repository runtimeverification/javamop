#JavaMOP INSTALL

Here are instructions for installing JavaMOP from its binary release
zip archive. Users who checked out the sources should follow the
instructions in src/README.md to build JavaMOP.

##Prerequisites

To use JavaMOP you need JRE, AJC and RV-Monitor.

1. JDK

 Java Development Kit (JDK) version 7.0.0 or higher 
 (http://www.oracle.com/technetwork/java/javase/downloads/index.html)

* Check Java is installed properly: run `java -version` and `jar` from
  a terminal. You can add `<JAVA_HOME>/bin` to your `PATH`.

* Note: The reason JDK is required here but not JRE is that the script
  `jar` is not included in JRE, but it is needed when using agent in
  JavaMOP.

2. AJC

 AspectJ Compiler version 1.8.1 or higher
 (http://www.eclipse.org/aspectj/downloads.php)

 * Make sure that `<AspectJ_HOME>/bin` is in your `PATH` by
   calling `ajc` in a terminal.

 * Also ensure that `<AspectJ_HOME>/lib/aspectjrt.jar`,
   `<AspectJ_HOME>/lib/aspectjweaver.jar` and
   `<AspectJ_HOME>/lib/aspectjtools.jar` are in your
   CLASSPATH.

3. RV-Monitor

 The latest version of RV-Monitor libraries 
 (https://www.runtimeverification.com/monitor)

 1. Download RV-Monitor installer (from the website above) and install
 it to your desired directory (referred as `<RV-Monitor_HOME>`).

 2. Add `<RV-Monitor_HOME>/lib/rvmonitor.jar` and
 `<RV-Monitor_HOME>/lib/rvmonitorrt.jar` to your CLASSPATH.

##Install

1. Download JavaMOP's binary from [its website]
(http://fsl.cs.illinois.edu/index.php/JavaMOP4).

2. Unzip the file to your preferred location.

##Use

`<path-to-javamop>/bin/javamop` is the target binary. For convenient
usage, update your `PATH` environment variable with
`<path-to-javamop>/bin/`.

See README.md for info on how to use JavaMOP.

#Troubleshooting

To get help or report problems go to
[Github](https://github.com/runtimeverification/javamop/issues)
