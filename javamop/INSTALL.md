# Installing JavaMOP from Binaries

Here are instructions for installing JavaMOP from its binary release
zip archive. Users who checked out the source code should follow the
instructions in [src/README.md](src/README.md) to build JavaMOP.

## Prerequisites

JavaMOP requires JDK, AJC and RV-Monitor.

1. [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
v.7 or higher
 * Check Java is installed properly: run `java -version` from a
  terminal.
2. [AJC](http://www.eclipse.org/aspectj/downloads.php)
v.1.8 or higher
 * Check AspectJ is installed properly: run `ajc -version` from a
   terminal.
 * Add `<AspectJ_HOME>/lib/aspectjrt.jar`,
   `<AspectJ_HOME>/lib/aspectjweaver.jar` and
   `<AspectJ_HOME>/lib/aspectjtools.jar` to your CLASSPATH.
3. [RV-Monitor](https://www.runtimeverification.com/monitor)
v.1.3 or higher
 * Check RV-Monitor is installed properly: run `rv-monitor -version` from a
   terminal.
 * Add `<RV-Monitor_HOME>/lib/rv-monitor.jar` and
 `<RV-Monitor_HOME>/lib/rv-monitor-rt.jar` to your CLASSPATH.

## Install

1. Download the binary JavaMOP release from
   [its website](http://fsl.cs.illinois.edu/index.php/JavaMOP4).

2. Unzip it to your preferred location and add
   `<JavaMOP_HOME>/bin` to your PATH.

3. Check JavaMOP is installed properly: run `javamop` from a
   terminal.

See [README.md](README.md) for more information.
Get help or report problems on
[JavaMOP's issues page](https://github.com/runtimeverification/javamop/issues).
