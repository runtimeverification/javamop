This file contains instructions for installing JavaMOP.

===1. Prerequisites===

=1.1 JDK =

We expect java 1.7. If you already have an installed JDK check the
version with javac -version. If you already have the correct JDK
installed make sure your environment variables are properly defined as
outlined below.

You may download JDK 7 from the following URL:

http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html

Download the right JDK file for your platform (you may be required to
accept the Oracle License Agreement).

To install JDK 7, follow the instructions found here:

http://docs.oracle.com/javase/7/docs/webnotes/install/

In UNIX-based Operating systems like Linux, set the PATH environment
variable to include the path to your newly installed JDK 7. Depending
on your operating system, the command may differ. For example, here is
a typical examples for Linux:

$ export PATH=$PATH:/usr/bin/java

In Windows, add the JDK 7 directory to your Path environment variable.

To test: run 'javac' from the command line, this should display usage
information for javac.

= Maven 3 =

JavaMOP requires Maven 3.0 or higher to build. To download and install
Maven 3.0, please follow the instructions on this page:

http://maven.apache.org/download.cgi

= Git 1.8 or higher = 

Git is required to download the sources for JavaMOP. To download and
install Git, please follow the instructions found on the following
page:

http://git-scm.com/book/en/Getting-Started-Installing-Git

===2. Build JavaMOP from Source===

Building JavaMOP is necessary only if you need to make modifications
to JavaMOP.  We are working on building a binary installer for
JavaMOP. When that becomes available, it is recommended that end users
use the JavaMOP binary installer and skip this section.

=2.1 Download the JavaMOP source code

As the tool is under active development, it is recommended that you
download JavaMOP directly from the Github repository:

git clone https://github.com/runtimeverification/javamop.git

Alternatively, you may also download the latest version of the code as
a "zip" or "tar.gz" archive from here:

https://github.com/runtimeverification/javamop/releases

=2.2 Build JavaMOP

From the top level directory of JavaMOP (i.e. the directory this file
is in) run the following commands:

mvn package 

This will download many dependencies the first time you run it. Note
that we are working on adding unit tests to JavaMOP at this time.

If you would like to build JavaMOP, run the integration tests and put
the javamop jar to be one of the jars that Maven knows about
(typically stored in a directory called .m2, which may be found in the
user's home directory), run the following command:

mvn install

===3. JavaMOP Setup===

=3.1 Setting Environment Variables

Set the PATH (in UNIX-based systems) or Path (In Windows) environment
to include the following JavaMOP binary directory:

path-to-JavaMOP/target/release/javamop/bin (in UNIX-based systems)

or

path-to-JavaMOP/target/release/javamop/bin (in Windows)

--- To test: type 'javamop' on the command line, this should display
usage information for javamop.

4. Contact Information

If you experience any problems installing JavaMOP, please open a new
issue on Github: https://github.com/runtimeverification/javamop/issues