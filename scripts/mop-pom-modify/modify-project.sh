#!/bin/bash

if [ $# -ne 2 ]; then
    echo "arg1 - the path to the project, where high-level pom.xml is"
    echo "arg2 - the tool we want to integrate [javamop,predict,ekstazi,cobertura]"
    exit
fi

project_path=$1
tool=$2

crnt=`pwd`
working_dir=`dirname $0`

cd ${project_path}
find . -name pom.xml | xargs git checkout -f
cd - > /dev/null

cd ${working_dir}

if [[ ${tool} == "javamop" ]] || [[ ${tool} == "predict" ]] || [[ ${tool} == "ekstazi" ]]; then
    javac PomFile.java
    find ${project_path} -name pom.xml | java -cp . PomFile ${tool}
elif [[ ${tool} == "cobertura" ]]; then
    javac PomFileCobertura.java
    find ${project_path} -name pom.xml | java -cp . PomFileCobertura ${tool}
fi

cd ${crnt}
