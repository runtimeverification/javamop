#!/usr/bin/env zsh
T=`pwd`
mv Test.java Test.bak.tmp
make
mv Test.bak.tmp Test.java
cd ../../../..
for i in $T/tests/* ; do
   for j in $i/*.in; do
      echo $j
      java javamop.LogicPluginShells.JavaCFG.CFGUtil.GLRGenTest "$i/cfg" > $T/Foo.java
      javac $T/Foo.java $T/Test.java || exit 1
      java javamop.LogicPluginShells.JavaCFG.CFGUtil.Test "$i/cfg" < "$j" > $j:r.tmp
      diff -y --suppress-common-lines $j:r.out $j:r.tmp
      rm $j:r.tmp
   done
done
