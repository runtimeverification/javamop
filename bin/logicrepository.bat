@echo off


java -cp "$INSTALL_PATH\lib\logicrepository.jar;$INSTALL_PATH\lib\plugins\*.jar;$INSTALL_PATH\lib\external\mysql-connector-java-3.0.9-stable-bin.jar" logicrepository.Main %*


