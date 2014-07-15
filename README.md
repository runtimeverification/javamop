
                          JavaMOP 2.3.1 README

==1. Overview==
Monitoring-Oriented Programming, abbreviated MOP, is a software development and
analysis framework aiming at reducing the gap between formal specification and
implementation by allowing them together to form a system. In MOP, runtime
monitoring is supported and encouraged as a fundamental principle for building
reliable software: monitors are automatically synthesized from specified
properties and integrated into the original system to check its dynamic
behaviors during execution. When a specification is violated or validated at
runtime, user-defined actions will be triggered, which can be any code from
information logging to runtime recovery. One can understand MOP from at least
three perspectives: as a discipline allowing one to improve safety, reliability
and dependability of a system by monitoring its requirements against its
implementation at runtime; as an extension of programming languages with logics
(one can add logical statements anywhere in the program, referring to past or
future states); and as a lightweight formal method. 

JavaMOP is an instance of MOP for Java.

==2. Usage==

If you want to use the Logic Repository Server provided by UIUC, use the
-remote option when using the 'javamop' script.

If you want to use the Logic Repository included in this package, use the
-local option when using the 'javamop' script.

Using the remote repository is the preferred method for computers with viable
Internet connections as it allows us to collect usage statistics used to improve
JavaMOP.

The 'javamop' script has the following usage:

Usage) javamop [-v] [-d <target directory>] <specification file or dir>

-v option is verbose mode -d option is used to specify the target directory
where the resulting aspectj code will be saved. Specification files must have
The .mop file extension.

Example) javamop -d examples/FSM/ examples/FSM/HasNext.mop

For more options, type 'javamop' or 'javamop -h'

==3. Additional Features of the JavaMOP Distribution==

=3.1 Executing a Monitored Program

When you execute a monitored program, you need to include the AspectJ library
and RV-Monitor Runtime Library in your class path. 

For more information, see the web documentation here: 
http://runtimeverification.com/monitor/docs/runningexamples.html#preparation


=3.2 Running the Logic Repository Tool

JavaMOP uses the Logic Repository automatically. Therefore, you do not need to
invoke the Logic Repository explicitly. JavaMOP will connect to the Logic
Repository automatically and retrieve a monitor for the given specification.
However, it is possible to use the Logic Repository directly for other uses.

To use the LogicRepository, type 

  'logicrepository'

And provide input through the standard input. For the XML syntax of input, refer
to the following pages:

  http://fsl.cs.uiuc.edu/index.php/Special:LogicRepository2.3

The resulting monitoring code is piped to standard output, in XML format.

=4. Contact Information

We welcome your interest in JavaMOP. Your feedback, comments and bug reports are
highly appreciated. Please feel free to contact us by sending email to
mop@cs.uiuc.edu. Bugs and feature requests may be submitted at the project
website at javamop.googlecode.com.


For more information, please see http://fsl.cs.illinois.edu/index.php/Special:JavaMOP3
