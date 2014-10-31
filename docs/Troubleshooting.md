#Troubleshooting

Here we gathered some problems that you might encounter during
installing or using JavaMOP, along with instructions on how to
solve them.

## I get "Code size too big" error when using AspecJ Compiler, what should I do?

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

## I get error when I use Xbootclasspath, what should I do ?

Instrumenting with Xbootclasspath can lead to errors if the right
jar files are not passed to the java command after weaving. 
For example, one may see the following error message when 
running something with Xbootclasspath:

	Error occurred during initialization of VM
	java.lang.NoSuchMethodError: sun.misc.JavaLangAccess.registerShutdownHook(ILjava/lang/Runnable;)V
    		at java.io.Console.<clinit>(Console.java:493)
    		at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    		at sun.misc.SharedSecrets.getJavaIOAccess(SharedSecrets.java:93)
    		at java.lang.System.initializeSystemClass(System.java:1089)
    		
The minimum necessary (for JDK 1.6.0.24 on a Linux OS) is `-Xbootclasspath/p:directoryWithInstrumentedJRE:/usr/lib/jvm/java-6-sun-1.6.0.24/jre/lib/rt.jar`

