javac props/classes/mop/*.java

javamopagent -emop props/ props/classes -n JavaMOPAgent -v

mv JavaMOPAgent.jar props/

mvn install:install-file -Dfile=props/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"

cd /tmp/commons-fileupload

rm -rf violation-counts

mvn test -Dtest=org.apache.commons.fileupload2.FileItemHeadersTest
