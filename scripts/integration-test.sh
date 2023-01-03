SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )

emop_path=$1

if [[ -n ${emop_path} ]]; then
    dir=${emop_path}/scripts
else
    dir=${SCRIPT_DIR}
fi

ASPECTJ_DIR=${SCRIPT_DIR}/aspectj1.8

if [ ! -d ${ASPECTJ_DIR} ]; then
    (
        cd ${SCRIPT_DIR}
        wget https://www.cs.cornell.edu/courses/cs6156/2020fa/resources/aspectj1.8.tgz
        tar -xzf aspectj1.8.tgz && rm aspectj1.8.tgz
    )
fi

function check_status() {
    local myvar=$1
    local suffix=$2
    
    if [[ -n ${myvar} ]]; then
        echo "PASSED "${suffix}
    else
        echo "FAILED "${suffix}
        exit 1
    fi
}

function check_reverse_status() {
    local myvar=$1
    local suffix=$2
    
    if [[ -z ${myvar} ]]; then
        echo "PASSED "${suffix}
    else
        echo "FAILED "${suffix}
        exit 1
    fi
}

(
    cd ${SCRIPT_DIR}/..
    git checkout -f master
    mvn clean package -DskipITs
) &> /tmp/mop-unit-tests.txt

mop_test_status=$(grep "BUILD SUCCESS" /tmp/mop-unit-tests.txt)
check_status "${mop_test_status}" " on mop's unit tests"

export PATH=${ASPECTJ_DIR}/bin:${ASPECTJ_DIR}/lib/aspectjweaver.jar:${SCRIPT_DIR}/../javamop/bin:${SCRIPT_DIR}/../rv-monitor/bin:${SCRIPT_DIR}/../rv-monitor/target/release/rv-monitor/lib/rv-monitor-rt.jar:${SCRIPT_DIR}/../rv-monitor/target/release/rv-monitor/lib/rv-monitor.jar:$PATH

export CLASSPATH=${ASPECTJ_DIR}/lib/aspectjtools.jar:${ASPECTJ_DIR}/lib/aspectjrt.jar:${ASPECTJ_DIR}/lib/aspectjweaver.jar:${SCRIPT_DIR}/../rv-monitor/target/release/rv-monitor/lib/rv-monitor-rt.jar:${SCRIPT_DIR}/../rv-monitor/target/release/rv-monitor/lib/rv-monitor.jar:$CLASSPATH

(
    rm -rf ${HOME}/.m2/repository/javamop
    rm -rf ${SCRIPT_DIR}/agents
    cd ${dir}
    git checkout -f master
    git clean -ffxd
    bash make-agent.sh ${SCRIPT_DIR}/props ${SCRIPT_DIR}/agents "quiet" "no-track" ${SCRIPT_DIR}/logs JavaMOPAgent
    # bash make-agent.sh props/ ${SCRIPT_DIR}/agents quieto
    mvn install:install-file -Dfile=${SCRIPT_DIR}/agents/JavaMOPAgent.jar -DgroupId="javamop-agent" -DartifactId="javamop-agent" -Dversion="1.0" -Dpackaging="jar"
) &> /tmp/agent-outcome.txt

agent_status=$(grep "JavaMOPAgent.jar is generated." /tmp/agent-outcome.txt)
check_status "${agent_status}" " on agent generation"

install_status=$(grep "BUILD SUCCESS" /tmp/agent-outcome.txt)
check_status "${install_status}" " on agent installation"

error_status=$(grep "error" /tmp/agent-outcome.txt)
echo ${error_status}

(
    if [ ! -d /tmp/commons-fileupload ]; then
        git clone https://github.com/apache/commons-fileupload /tmp/commons-fileupload
    fi
    cd /tmp/commons-fileupload
    git clean -ffxd
    git checkout -f 55dc6fe4d7
    bash ${dir}/mop-pom-modify/modify-project.sh `pwd` javamop
    export RVMLOGGINGLEVEL=UNIQUE
    mvn test
) &> /tmp/fileupload-outcome.txt

fileupload_status=$(grep "BUILD SUCCESS" /tmp/fileupload-outcome.txt)
check_status "${fileupload_status}" " on fileupload tests"

# This test is super weak; but JavaMOP on fileupload seems
# non-deterministic; need to think of a better way

violation_diffs=$(diff <(cut -d' ' -f3 /tmp/commons-fileupload/violation-counts | sort | uniq) <(cut -d' ' -f3 ${SCRIPT_DIR}/commons-fileupload-violation-counts | sort | uniq))
check_reverse_status "${violation_diffs}" "on fileupload violations"
