# Building JavaMOP from Source Code

This file contains instructions to build JavaMOP from source code.
If you only want to be a user, but not a developer of JavaMOP, then we
recommend that you install it from its
[binary release zip archive](http://fsl.cs.illinois.edu/index.php/JavaMOP4);
in that case, please refer to [../INSTALL.md](../INSTALL.md).

## Prerequisites

All the prerequisites in [../INSTALL.md](../INSTALL.md) for users
installing from binaries, plus:

1. [Maven](http://maven.apache.org/download.cgi)
v.3.0 or higher
 * Check Maven is installed properly: run `mvn -version` from a terminal.
2. [Git](http://git-scm.com/book/en/Getting-Started-Installing-Git)
v.1.8 or higher
 * Check Git is installed properly: run `git` from a terminal.

## Build and Install

1. Run `git clone https://github.com/runtimeverification/javamop.git`
to check out the source code from the
[Github repository](https://github.com/runtimeverification/javamop/)
(or download it as a ZIP or TAR.GZ archive directly from the
[Github release page](https://github.com/runtimeverification/javamop/releases)).

2. To prepare to build JavaMOP, you must install RV-Monitor to your local Maven
repository.  If you do not have access to the proprietary RV Maven repository run
`mvn validate -DrvMonitorBase=[path to RV-Monitor base dir]` from the 
`<JavaMOP_HOME>` directory.  No failures should be reported.

3. Run `mvn package` in the `<JavaMOP_HOME>` directory.
This will download many dependencies the first time you run it.

4. Add `<JavaMOP_HOME>/bin` to your PATH.

5. Check JavaMOP is installed properly: run `javamop` from a
   terminal.

See [../README.md](../README.md) for more information.
Get help or report problems on
[JavaMOP's issues page](https://github.com/runtimeverification/javamop/issues).
