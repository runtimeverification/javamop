#!/bin/bash

SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )

if [ $# != 3 ]; then
    echo "Usage: $0 property-directory output-directory mode"
    echo "       mode: {verbose|quiet}"
    exit
fi

props_dir=$1
out_dir=$2
mode=$3

function build_agent() {
    local agent_name=$1
    # This is how to build JavaMOP agents as of 03/23/2016.
    # https://github.com/runtimeverification/javamop/blob/master/docs/JavaMOPAgentUsage.md
    local prop_files=${props_dir}/*.mop
    for spec in ${prop_files}
    do
      javamop -baseaspect ${SCRIPT_DIR}/BaseAspect.aj -emop ${spec}
    done
    rm -rf ${props_dir}/classes/mop; mkdir -p ${props_dir}/classes/mop
    rv-monitor -merge -d ${props_dir}/classes/mop/ ${props_dir}/*.rvm #-v
    javac ${props_dir}/classes/mop/*.java
    # rm ${props_dir}/classes/mop/*.java
    cp ${SCRIPT_DIR}/BaseAspect_new.aj ${props_dir}/BaseAspect.aj
    if [ "${mode}" == "verbose" ]; then
        echo "AGENT IS VERBOSE!"
        javamopagent -m -emop ${props_dir}/ ${props_dir}/classes -n ${agent_name} -v
    elif [ "${mode}" == "quiet" ]; then
        echo "AGENT IS QUIET!"
        javamopagent -emop ${props_dir}/ ${props_dir}/classes -n ${agent_name} -v
    fi
    mv ${agent_name}.jar ${out_dir}
}

mkdir -p ${out_dir}
build_agent JavaMOPAgent
