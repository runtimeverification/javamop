.PHONY: clean test
YACC=javacc
CC=javac

all: cfg.jj.javacc
	${CC} *.java > /dev/null

test: all
	./test.sh

cfg.jj.javacc: cfg.jj
	${YACC} cfg.jj > /dev/null


clean:
	rm *.class CFGParserConstants.java ParseException.java SimpleCharStream.java TokenMgrError.java Token.java CFGParserTokenManager.java CFGParser.java
