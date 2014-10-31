# JavaMOP README

## Overview

Monitoring-Oriented Programming (MOP), is a software development and
analysis framework which aims to reduce the gap between formal
specification and implementation by allowing them together to form a
system. In MOP, runtime monitoring is supported and encouraged as a
fundamental principle for building reliable software: monitors are
automatically synthesized from specified properties and integrated
with the original system to check its dynamic behaviors during
execution. When a specification is violated or validated at runtime,
user-defined actions will be triggered, which can be any code: from
information logging to runtime recovery.  MOP may be understood from
at least three perspectives: (a) as a discipline allowing one to
improve safety, reliability and dependability of a system by
monitoring its requirements against its implementation at runtime; (b)
as an extension of programming languages with logics (one can add
logical statements anywhere in the program, referring to past or
future states); and (c) as a lightweight formal method.

JavaMOP is an instance of MOP for Java.

## Install

Refer to the INSTALL.md file for installing JavaMOP from the [released
zip archive](http://fsl.cs.illinois.edu/index.php/JavaMOP4).  To build
JavaMOP from sources, please download the [source
code](https://github.com/runtimeverification/javamop), and refer to
the src/README file.


## Usage

Refer to Usage.md in docs folder for instructions on how to use JavaMOP.

### Troubleshooting

In some extreme cases when you need to monitor classes with huge
methods or the intended pointcuts are very dense in some method, you
may encounter some error while using the standard AspectJ compiler to
weave code; as follows:

`/YourBigClass.java [error] problem generating method YourBigClass.bigMethod: Code size too big: 65613.` 

This error is caused by Java's 64KB maximum method size constraint.
If a method of the monitored class is already too big, then, after
inserting the advice at the pointcuts, it may exceed the 64KB
limit.

If you have access to the source code (.java files) of the program
that you want to monitor, then the easiest way to solve this problem
is weaving the source code (.java files) instead of weaving the
compiled code (.class). The AspectJ compiler optimizes the source code
to make it more space-efficient so that it will not violate the method
size constraint during weaving. This error has nothing to do with
JavaMOP, it is caused by AspectJ's limitation. This kind of problem
often happens when you try to weave the bytecode (.class files) and
aspects. If you have access to the source code (.java files) of the
program that you want to monitor, then the easiest way that is likely
to solve this problem is weaving the source code (.java files) instead
of weaving the compiled code (.class files). AspectJ compiler will do
some optimization to the source code to make it more space-efficient
so that the method size may reduce to the safe zone.

In case you encounter such problem, but either you do not have access to
the source code, or, the above method does not work, then we also provide
a patch for the standard AspectJ source code to solve this problem. 
Please follow the instructions below to install the patch (Please backup
your AspectJ before applying the patch):

**Prerequisites for using the patch:**
 
* AspectJ Compiler source code.

  If you have not checked out the source code of AspectJ, you can go
  to [here](http://git.eclipse.org/c/aspectj/org.aspectj.git) to check
  it out.
  
* Ant.

  If you do not have Ant installed, then go to
  [here](http://ant.apache.org/) to download and install the latest
  version.

1. Go to your local AspectJ git repository, and checkout the commit
`Fix 443355: interface super references` by executing the command
below (You may need to pull to get the latest updates before you can
perform the operation below):

	``git checkout dddd1236cd21982a07f887ff7fa5d484ebc3b86c``

2. Download the `stdAJC_dddd123.patch` from 
[here](http://fsl.cs.illinois.edu/downloads/stdAJC_dddd123.patch),
and place the patch file in the above AspectJ repository's top level.

3. Apply the patch by executing:

	``git apply --whitespace=nowarn stdAJC_dddd123.patch``
	
4. Build the AspectJ project using ant. You can execute the command
``ant`` at the top level of the AspectJ repository.

5. Deploy the AspectJ libraries. If the previous step is successful,
there will be a new folder called `aj-build` appearing at the top
level of the repository. Add all the jar files in
`<path-to-AspectJ-repository>/aj-build/dist/tools/lib` to your
CLASSPATH.

After generating the new AspectJ libraries and deploying them, your
AspectJ compiler should now be able to handle the classes with huge
methods.

####Complete list for troubleshooting 

If your problem is not listed in the above section, you can go to
[here](http://fsl.cs.illinois.edu/index.php/JavaMOP4_Troubleshooting)
for a complete list of common issues and their solutions.

## Contact Information

We welcome your interest in JavaMOP. Your feedback, comments and bug
reports are highly appreciated. Please feel free to contact us by
opening new issues on [JavaMOP's issues page]
(https://github.com/runtimeverification/javamop/issues).
