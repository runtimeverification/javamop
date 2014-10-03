#JavaMOP INSTALL

This file contains instructions for installing JavaMOP. 

##Prerequisites

To use JavaMOP you only need JDK. However, you'll need Maven and Git if you want to build JavaMOP from the its source.

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

##Install

You may either download JavaMOP's source code and locally build it ***or*** directly download it's pre-packaged binary and start using it.

###Option 1: Build JavaMOP from Source

1. Download the JavaMOP source code From the Github repository:

 ```git clone https://github.com/runtimeverification/javamop.git```

 You can also download a "zip" or "tar.gz" from [here](https://github.com/runtimeverification/javamop/releases) and unzip to your favorite location.

2. Build JavaMOP

 From the top level directory of JavaMOP (i.e. the directory this
 file is in) run the following commands:

 ```mvn package```

 This will download many dependencies the first time you run it.

###Option 2: Download pre-packaged binary
   
1. Download JavaMOP binary file from [its website](http://fsl.cs.illinois.edu/javamop).

2. Unzip or untar JavaMOP binary file in your preferred location.

##Using JavaMOP 

In either case, `<path-to-javamop>/bin/javamop` is the target binary. For convenient usage, update your $PATH environment variable with `<path-to-javamop>/bin/`.

For information regarding usage of JavaMOP, please refer to the README file.

#Contact Information

If you experience any problems installing JavaMOP, please open a new
issue on [Github](https://github.com/runtimeverification/javamop/issues)
