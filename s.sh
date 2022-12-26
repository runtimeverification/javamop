# javac props/classes/mop/*.java

# javamopagent -m -emop props/ props/classes -n JavaMOPAgent -v

# mv JavaMOPAgent.jar agents

mvn install:install-file -Dfile=agents/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"

cd /tmp/commons-fileupload

rm -rf violation-counts

rm -rf /tmp/.traces/internal.txt
rm -rf /tmp/.traces/traces.txt
rm -rf /tmp/.traces/locations.txt
rm -rf /tmp/.traces/unique-traces.txt

time mvn test -Drat.skip &> gol.txt

tail /tmp/.traces/internal.txt
tail /tmp/.traces/traces.txt
tail /tmp/.traces/locations.txt
tail /tmp/.traces/unique-traces.txt
