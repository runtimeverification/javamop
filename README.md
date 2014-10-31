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

Refer to the INSTALL.md for installing JavaMOP from the [released
zip archive](http://fsl.cs.illinois.edu/index.php/JavaMOP4).  To build
JavaMOP from sources, please download the [source
code](https://github.com/runtimeverification/javamop), and refer to
the src/README.md.


## Usage

Refer to docs/Usage.md for instructions on how to use JavaMOP.

## Troubleshooting

If you experience any problems when installing or using JavaMOP,
and can not find your answer in docs/Troubleshooting.md,
open a new issues on [JavaMOP's issues page]
(https://github.com/runtimeverification/javamop/issues).

