#JavaMOP BUILD

This file contains instructions for building JavaMOP from its source code. 
If you only want to use JavaMOP out of the box please refer to ../INSTALL.md

##Prerequisites

1. JDK

 Java Runtime Edition version 7 (http://java.com/en/download/index.jsp)
 * To make sure java is installed properly, call `java -version` in a terminal.

2. Maven 3 or higher

 JavaMOP requires Maven 3.0 or higher to build. To download and
 install Maven 3.0, please follow the instructions on 
 [this page](http://maven.apache.org/download.cgi)

3. Git 1.8 or higher

 Git is required to download the sources for JavaMOP. To
 download and install Git, please follow the instructions found
 on [this page](http://git-scm.com/book/en/Getting-Started-Installing-Git)

##Build

1. Download the JavaMOP source code From the Github repository:

 ```git clone https://github.com/runtimeverification/javamop.git```

 You can also download a "zip" or "tar.gz" from [here](https://github.com/runtimeverification/javamop/releases) and unzip to your favorite location.

2. Build JavaMOP

 From the top level directory of JavaMOP (i.e. the directory this
 file is in) run the following commands:

 ```mvn package```

 This will download many dependencies the first time you run it.


##Use 

After building the project, `<path-to-javamop>/bin/javamop` is the target binary. For convenient usage, update your $PATH environment variable with `<path-to-javamop>/bin/`.

For information regarding usage of JavaMOP, please refer to the ../README.md file.

#Troubleshooting

If you experience any problems building JavaMOP, please open a new
issue on [Github](https://github.com/runtimeverification/javamop/issues)
