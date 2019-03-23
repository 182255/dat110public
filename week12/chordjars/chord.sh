#!/bin/sh

cd project3/bin
for index in {1..10}
do
	echo "Main-Class: no.hvl.dat110.node.client.test.Process$index\n" > Manifest.txt
	jar -cfm chorddht$index.jar Manifest.txt . *.class
	mv chorddht$index.jar ../
done