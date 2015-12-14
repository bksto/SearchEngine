#!/bin/bash

echo Compiling Indexer...

javac -cp .:lucene-core-3.6.0.jar Indexer.java WebDocument.java

echo Running Indexer...
java -cp .:lucene-core-3.6.0.jar Indexer $1

