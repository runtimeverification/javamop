# javac props/classes/mop/*.java

# javamopagent -m -emop props/ props/classes -n JavaMOPAgent -v

# mv JavaMOPAgent.jar agents

mvn install:install-file -Dfile=agents/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"

cd /tmp/commons-fileupload

rm -rf violation-counts

rm -rf /tmp/internal.txt
rm -rf /tmp/traces.txt
rm -rf /tmp/locations.txt
rm -rf /tmp/unique-traces.txt

time mvn test -Drat.skip &> gol.txt

tail /tmp/internal.txt
tail /tmp/traces.txt
tail /tmp/locations.txt
tail /tmp/unique-traces.txt
