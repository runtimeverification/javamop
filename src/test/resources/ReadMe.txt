This folder stores several .ser files which are the serialization form of
MOPSpecFile objects. They are used as testing resources, and the process 
of generating these files is described below:

1. Clone JavaMOP's git repository and checkout V4.4
```git clone https://github.com/runtimeverification/javamop.git```
```cd javamop```
```git checkout 4.4```

2. Run the main method of class ```src\test\java\unit\javamop\helper\MOP_Serialization.java```

3. The output serialization files will be generated in this directory.

The idea is if we trust the correctness of mop files parsing function provided
in JavaMOP version 4.4, then we can use its results to judge other 'untrusted'
parser implementations. 

We can use the parser in version 4.4 to parse the mop files, generate their
canonical AST representations, and serialize them to files. Later, when we
refactored the parser, we can deserialize the .ser file to get the correct
MOPSpecFile representation of the target mop file, comparing it with the 
MOPSpecFile object parsed by the new parser in order to evaluate the
correctness of the new parser.