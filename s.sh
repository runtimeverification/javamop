mvn install:install-file -Dfile=agents/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"

cd /tmp/commons-fileupload

rm -rf violation-counts

rm -rf /tmp/internal.txt

mvn test -Dtest=org.apache.commons.fileupload2.FileItemHeadersTest

tail /tmp/internal.txt
