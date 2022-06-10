if [[ $1 == "build" ]]; then
    mvn clean package -DskipTests -DskipITs
fi

rv-monitor -d out -debug ${HOME}/projects/javamop/rv-monitor/examples/java/FSM/HasNext2/rvm -v

# rv-monitor -d out -debug ${HOME}/projects/javamop/rv-monitor/examples/java/ERE/UnsafeMapIterator/rvm -v

# rv-monitor -d out -debug ${HOME}/projects/javamop/rv-monitor/examples/java/CFG/SafeFileWriter/rvm -v

# rv-monitor -d out -debug ${HOME}/projects/javamop/rv-monitor/examples/java/SRS/EqualityCheck/rvm -v

# rv-monitor -d out -debug ${HOME}/projects/javamop/rv-monitor/examples/java/LTL/SafeMapIterator/rvm -v
