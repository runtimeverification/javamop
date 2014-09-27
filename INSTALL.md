This file contains instructions for installing JavaMOP.

1. Prerequisites

  1. JDK

     We expect java 1.7. If you already have an installed JDK check
     the version with javac -version. If you need to install this
     version of JDK, please follow the instrcutions found on [this
     page](http://docs.oracle.com/javase/7/docs/webnotes/install/)

     Set the PATH or Path enviroment variable to include the path to
     your newly installed JDK 7. For example, here is a typical
     command for doing so in Linux:

     ```$ export PATH=$PATH:/usr/bin/java```

  2. Maven 3

      JavaMOP requires Maven 3.0 or higher to build. To download and
      install Maven 3.0, please follow the instructions on 
      [this page](http://maven.apache.org/download.cgi)

  3. Git 1.8 or higher

       Git is required to download the sources for JavaMOP. To
       download and install Git, please follow the instructions found
       on [this page](http://git-scm.com/book/en/Getting-Started-Installing-Git)

2. Build JavaMOP from Source

  1. Download the JavaMOP source code

     From the Github repository:

     ```git clone https://github.com/runtimeverification/javamop.git```

     As a "zip" or "tar.gz":
     [here](https://github.com/runtimeverification/javamop/releases)

  2. Build JavaMOP

      From the top level directory of JavaMOP (i.e. the directory this
      file is in) run the following commands:

      ```mvn package```

      This will download many dependencies the first time you run it.

3. JavaMOP Setup

  1. Setting Environment Variables

     Set the PATH (in UNIX-based systems) or Path (In Windows)
     environment to include the following JavaMOP binary directory:

     ```path-to-JavaMOP/target/release/javamop/bin``` (on UNIX-based systems)

     or

     ```path-to-JavaMOP\target\release\javamop\bin``` (on Windows)

     To test: type 'javamop' on the command line, this should
     display usage information for javamop.

4. Contact Information

If you experience any problems installing JavaMOP, please open a new
issue on [Github](https://github.com/runtimeverification/javamop/issues)
