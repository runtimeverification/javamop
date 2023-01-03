# JavaMOP

Improved and integrated source code that was forked off the [JavaMOP](https://github.com/runtimeverification/javamop) and [RV-Monitor](https://github.com/runtimeverification/rv-monitor) repos, which are no longer maintained.

## Prerequisites

We have only tested JavaMOP on:

1. Java 1.8
2. Maven 3.6.3 and above
3. Maven Surefire 2.14 and above
4. Operating System: Linux or OSX

## Setting up

1. From the same directory as this README.md file, run:

   a. `bash scripts/install-javaparser.sh`
   b. `bash scripts/integration-test.sh`

   The first script installs a modified version of [JavaParser]() that this version of JavaMOP depends on. The second command runs all the tests in JavaMOP, makes a JavaMOP agent, installs the JavaMOP agent, integrates the JavaMOP agent into the [Apache Commons FileUpload]() open-source project, then monitors the tests in that project against [161 specs]. So, understanding and running `scripts/integration-test.sh` is a good way to get started with using JavaMOP.

   NOTE: We are aware of one parsing-related flaky unit test in JavaMOP. When that test fails, the run of the second script will stop. One work-around is to change `mvn clean package -DskipITs` to `mvn clean package -DskipITs -DskipTests` in `scripts/integration-test.sh`. Another work-around is to comment out all occurrences of `exit 1` in `scripts/integration-test.sh`. We plan to fix these tests soon, but please feel free to contribute a pull request if you have a patch.

2. 

