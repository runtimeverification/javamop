@echo off

set SRC_ROOT=%~dp0..

java -cp "%SRC_ROOT%/lib/javamoptestsuite.jar;%SRC_ROOT%/lib/javamop.jar;%SRC_ROOT%/lib/logicrepository.jar:%SRC_ROOT%/lib/*.jar" javamoptestsuite.Main -local "%SRC_ROOT%/examples" %*

