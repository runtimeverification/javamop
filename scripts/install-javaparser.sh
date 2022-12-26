(
    cd /tmp
    git clone https://github.com/javaparser/javaparser.git
    (
        cd javaparser
        git checkout javaparser-parent-3.23.1
        sed -i.bak 's/public final int hashCode/public int hashCode/' javaparser-core/src/main/java/com/github/javaparser/ast/Node.java
        rm javaparser-core/src/main/java/com/github/javaparser/ast/Node.java.bak
        mvn install -DskipTests -DskipITs
    )
)
