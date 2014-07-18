@echo off

set SRC_ROOT=%~dp0..

java -cp "%CLASSPATH%;%SRC_ROOT%\lib\aspectj\aspectjtools.jar;%SRC_ROOT%\lib\aspectj\aspectjrt.jar" org.aspectj.tools.ajc.Main %*
