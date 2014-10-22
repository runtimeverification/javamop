@echo off

set SRC_ROOT=%~dp0..

set RELEASE=%SRC_ROOT%\lib

set PLUGINS=%RELEASE%\plugins
set LOGICPLUGINPATH=%PLUGINS%
set CP=%RELEASE%\*;%PLUGINS%\*
for /f %%a IN ('dir /b /s "%PLUGINS%\*.jar"') do call %concat% %%a

java -cp "%CP%;%CLASSPATH%" javamop.JavaMOPMain %*
