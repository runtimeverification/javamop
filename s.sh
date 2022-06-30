mvn install:install-file -Dfile=agents/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"

cd /tmp/commons-fileupload

rm -rf violation-counts

rm -rf /tmp/internal.txt
rm -rf /tmp/traces.txt

mvn test -Drat.skip &> gol.txt

tail /tmp/internal.txt
tail /tmp/traces.txt
