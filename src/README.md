#JavaMOP BUILD

This file contains instructions for building JavaMOP from its source
code.  If you only want to use JavaMOP from released zip archive
please refer to ../INSTALL.md

##Prerequisites

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

3. [Maven] (http://maven.apache.org/download.cgi) v.3.0 or higher

 * Check Maven is installed properly: run `mvn -version` from a
   terminal.

4. [Git] (http://git-scm.com/book/en/Getting-Started-Installing-Git) v.1.8 or higher

 * Check Git is installed properly: run `git` from a
   terminal.

##Build and Install

1. Check out the JavaMOP source code from the Github repository:

 ```git clone https://github.com/runtimeverification/javamop.git```

 Alternatively, you may directly download source code without using
 git from [here](https://github.com/runtimeverification/javamop/releases),
 and unpack it to your favorite location.

2. Build JavaMOP

 From the top level directory of JavaMOP (i.e. the parent directory
 this file is in) run the following commands:

 ```mvn package```

 This will download many dependencies the first time you run it.

3. Add `<JavaMOP_HOME>/bin` to your PATH.

4. Check JavaMOP is installed properly: run `javamop` from a
   terminal.

See ../README.md for more information. To get help or report problems go
to [JavaMOP's issues page]
(https://github.com/runtimeverification/javamop/issues).
