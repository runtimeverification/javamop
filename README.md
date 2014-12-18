# JavaMOP Overview

[Monitoring-Oriented Programming (MOP)](http://fsl.cs.illinois.edu/mop),
is a software development and analysis framework which aims to reduce
the gap between formal specification and implementation by allowing
them together to form a system.
In MOP, runtime monitoring is supported and encouraged as a
fundamental principle for building reliable software: monitors are
automatically synthesized from specified properties and integrated
with the original system to check its dynamic behaviors during
execution. When a specification is violated or validated at runtime,
user-defined actions will be triggered, which can be any code: from
information logging to runtime recovery. 

[JavaMOP](http://fsl.cs.illinois.edu/javamop)
is an instance of MOP for Java.

## Installation

See [INSTALL.md](INSTALL.md) for installing JavaMOP from the
[binary release zip archive](http://fsl.cs.illinois.edu/index.php/JavaMOP4).
To build JavaMOP from source code, download the
[source code](https://github.com/runtimeverification/javamop) and refer to
[src/README.md](src/README.md).

## Usage

Refer to [docs/Usage.md](docs/Usage.md) for detailed instructions on how
to use JavaMOP.

## JavaMOPAgent

A separate tool called ```javamopagent``` is provided to facilitate the 
monitoring of your program. Refer to [docs/JavaMOPAgentUsage.md]
(docs/JavaMOPAgentUsage.md) for detailed instructions on how to use 
JavaMOPAgent. 
