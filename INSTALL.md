#JavaMOP INSTALL

This file contains instructions for using JavaMOP out of the box. 
If you are a developer or want to build JavaMOP from its source code, please refer to src/README.md  

##Prerequisites

To use JavaMOP you need JRE, AJC and RVMonitor.

1. JRE

 Java Runtime Environment version 7 (http://java.com/en/download/index.jsp)
 * To make sure java is installed properly, call `java -version` in a terminal.
  
2. AJC

 AspectJ Compiler version 1.8.1 or higher (http://www.eclipse.org/aspectj/downloads.php)
 * Make sure that `<Path-to-AspectJ_HOME>/bin` is in your `PATH` by calling `ajc` in a terminal.
 * Also ensure that `<Path-to-AspectJ_HOME>/lib/aspectjrt.jar` in your CLASSPATH.
 
3. RVMonitor
   
 The RVMonitor libraries. (https://www.runtimeverification.com/monitor)
 1. Download `rvmonitorrt.jar` and `rvmonitor.jar` from the above website.
 2. Add `<path-to-rvmonitorrt.jar>` and `<path-to-rvmonitor.jar>` to your CLASSPATH.

##Install
   
1. Download JavaMOP's binary from [its website](http://fsl.cs.illinois.edu/javamop).
 
2. Unzip or untar the file in your preferred location.

##Use 

`<path-to-javamop>/bin/javamop` is the target binary. For convenient usage, update your `PATH` environment variable with `<path-to-javamop>/bin/`.

For information regarding usage of JavaMOP, please refer to the README.md file.

#Troubleshooting

If you experience any problems installing JavaMOP, please open a new
issue on [Github](https://github.com/runtimeverification/javamop/issues)
