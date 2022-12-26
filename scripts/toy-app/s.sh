mvn compile

mvn test-compile

mvn test

mvn surefire:test

# WHERE DID THE JARS GO?

pom.xml

# BUT THERE'S NO HAMCREST!

mvn dependency:list

mvn dependency:tree

# HOW DO I RUN MY PROGRAM?



# HOW ABOUT CREATING A MAVEN PROJECT FROM SCRATCH?

mvn archetype:generate -DgroupId=edu.cornell.cs5154 -DartifactId=in-class-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
