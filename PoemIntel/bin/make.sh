#!/bin/bash

javac ../src/poem/*.java -d .
echo "poem package compiled"
javac ../src/server/*.java -d .
echo "server package compiled"
