This file contains instructions for installing JavaMOP.

1. Prerequisites

  1. JDK

     Java Runtime Edition version 7 (http://java.com/en/download/index.jsp)
     * To make sure java is installed properly, call `java -version` in a terminal.

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
     [Here](https://github.com/runtimeverification/javamop/releases)

  2. Build JavaMOP

      From the top level directory of JavaMOP (i.e. the directory this
      file is in) run the following commands:

      ```mvn package```

      This will download many dependencies the first time you run it.

3. JavaMOP Setup

     For convenient usage, update your $PATH environment variable with <path-to-javamop>/bin/javamop

4. Contact Information

If you experience any problems installing JavaMOP, please open a new
issue on [Github](https://github.com/runtimeverification/javamop/issues)
